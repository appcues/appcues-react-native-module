//
//  ReactNativeElementTargeting.swift
//  appcues-react-native
//
//  Created by James Ellis on 3/21/23.
//

import AppcuesKit
import UIKit
import WebKit

internal class ReactNativeElementSelector: AppcuesElementSelector {
    let appcuesID: String?
    let nativeID: String?
    let testID: String?
    let tag: String?

    var displayName: String? {
        if let appcuesID = appcuesID {
            return appcuesID
        } else if let nativeID = nativeID {
            return nativeID
        } else if let testID = testID {
            return testID
        } else if let tag = tag {
            return "(tag \(tag))"
        }

        return nil
    }

    init?(
        appcuesID: String?,
        nativeID: String?,
        testID: String?,
        tag: String?
    ) {
        // must have at least one identifiable property to be a valid selector
        if appcuesID == nil &&  nativeID == nil && testID == nil && tag == nil {
            return nil
        }

        self.appcuesID = appcuesID
        self.nativeID = nativeID
        self.testID = testID
        self.tag = tag
        super.init()
    }

    override func evaluateMatch(for target: AppcuesElementSelector) -> Int {
        guard let target = target as? ReactNativeElementSelector else {
            return 0
        }

        // weight the selector property matches by how distinct they are considered
        var weight = 0

        if appcuesID != nil && appcuesID == target.appcuesID {
            weight += 1_000
        }

        if nativeID != nil && nativeID == target.nativeID {
            weight += 1_000
        }

        if testID != nil && testID == target.testID {
            weight += 1_000
        }

        if tag != nil && tag == target.tag {
            weight += 100
        }

        return weight
    }

    private enum CodingKeys: String, CodingKey {
        case appcuesID
        case nativeID
        case testID
        case tag
    }

    override func encode(to encoder: Encoder) throws {
        try super.encode(to: encoder)
        var container = encoder.container(keyedBy: CodingKeys.self)
        if let appcuesID = appcuesID, !appcuesID.isEmpty {
            try container.encode(appcuesID, forKey: .appcuesID)
        }
        if let nativeID = nativeID, !nativeID.isEmpty {
            try container.encode(nativeID, forKey: .nativeID)
        }
        if let testID = testID, !testID.isEmpty {
            try container.encode(testID, forKey: .testID)
        }
        if let tag = tag, !tag.isEmpty {
            try container.encode(tag, forKey: .tag)
        }
    }
}

@available(iOS 13.0, *)
internal class ReactNativeElementTargeting: AppcuesElementTargeting {
    @MainActor
    func captureLayout() async -> AppcuesViewElement? {
        return await UIApplication.shared.windows.first { !$0.isAppcuesWindow }?.captureLayout()
    }

    func inflateSelector(from properties: [String: String]) -> AppcuesElementSelector? {
        return ReactNativeElementSelector(
            appcuesID: properties["appcuesID"],
            nativeID: properties["nativeID"],
            testID: properties["testID"],
            tag: properties["tag"]
        )
    }
}

extension UIView {
    var compatibleNativeID: String? {
        #if RCT_NEW_ARCH_ENABLED
        guard responds(to: Selector(("nativeId"))) else {
            return nil
        }
        return value(forKey: "nativeId") as? String
        #else
        return nativeID
        #endif
    }

    var reactNativeSelector: ReactNativeElementSelector? {
        return ReactNativeElementSelector(
            appcuesID: nil,
            nativeID: compatibleNativeID.flatMap { $0.isEmpty ? nil : $0 },
            // on iOS, the "testID" set on a react native view comes in through
            // the accessibilityIdentifier property on the UIView
            testID: accessibilityIdentifier.flatMap { $0.isEmpty ? nil : $0 },
            tag: nil
        )
    }

    func captureLayout() async -> AppcuesViewElement? {
        return await self.asCaptureView(in: self.bounds, safeAreaInsets: self.safeAreaInsets)
    }

    private func asCaptureView(in bounds: CGRect, safeAreaInsets: UIEdgeInsets) async -> AppcuesViewElement? {
        let absolutePosition = self.convert(self.bounds, to: nil)

        // discard views that are not visible in the screenshot image
        guard absolutePosition.intersects(bounds) else { return nil }

        let childInsets = UIEdgeInsets(
            top: max(safeAreaInsets.top, self.safeAreaInsets.top),
            left: max(safeAreaInsets.left, self.safeAreaInsets.left),
            bottom: max(safeAreaInsets.bottom, self.safeAreaInsets.bottom),
            right: max(safeAreaInsets.right, self.safeAreaInsets.right)
        )

        let children: [AppcuesViewElement]

        if let webView = self as? WKWebView {
            let adjustment: CGPoint = webView.scrollView.contentInsetAdjustmentBehavior == .never
            ? absolutePosition.origin
            : absolutePosition.inset(by: childInsets).origin
            children = await webView.children(positionAdjustment: adjustment)
        } else {
            children = await self.subviews.asyncCompactMap {
                // discard hidden views and subviews within
                guard !$0.isHidden else { return nil }
                return await $0.asCaptureView(in: bounds, safeAreaInsets: childInsets)
            }
        }

        // find the rect of the visible area of the view within the safe area
        let safeBounds = bounds.inset(by: safeAreaInsets)
        let visibleRect = safeBounds.intersection(absolutePosition)

        // if there is no visible rect, fall back to the absolute position, but we will
        // not generate any selector for non-visible item below. Do not skip the item entirely
        // since it could have children that are within the visible range (out of bounds of parent)
        let locationRect = visibleRect.isNull ? absolutePosition : visibleRect

        // only create a selector for elements that have at least the center point
        // visible in the current screen bounds, inset by any safe area adjustments
        let centerPointVisible = safeBounds.contains(CGPoint(x: absolutePosition.midX, y: absolutePosition.midY))
        let selector = centerPointVisible ? reactNativeSelector : nil

        return AppcuesViewElement(
            x: locationRect.origin.x,
            y: locationRect.origin.y,
            width: locationRect.width,
            height: locationRect.height,
            type: "\(type(of: self))",
            selector: selector,
            children: children.isEmpty ? nil : children,
            displayName: selector?.displayName)
    }
}

@available(iOS 13.0, *)
private extension WKWebView {
    func children(positionAdjustment: CGPoint) async -> [AppcuesViewElement] {
        let script = """
        [...document.querySelectorAll('[id], [data-appcues-id]')].reduce((result, el) => {
            const { x, y, width, height } = el.getBoundingClientRect();
            const tag = el.id ? `#${el.id}` : null;
            const appcuesID = el.getAttribute('data-appcues-id')
            if (height !== 0 && width !== 0) {
                result.push({
                    x,
                    y,
                    width,
                    height,
                    tag,
                    appcuesID
                });
            }
            return result;
        }, []);
        """

        let response = try? await self.evaluateJavaScript(script)

        guard let objects = response as? [Dictionary<String, Any>] else { return [] }

        return objects.compactMap { element in
            guard let x = element["x"] as? CGFloat,
                  let y = element["y"] as? CGFloat,
                  let width = element["width"] as? CGFloat,
                  let height = element["height"] as? CGFloat
            else {
                return nil
            }

            let elementFrame = CGRect(x: x, y: y, width: width, height: height)
            guard self.bounds.contains(CGPoint(x: elementFrame.midX, y: elementFrame.midY)) else {
                return nil
            }

            let appcuesID = element["appcuesID"] as? String
            let tag = element["tag"] as? String

            return AppcuesViewElement(
                x: positionAdjustment.x + x,
                y: positionAdjustment.y + y,
                width: width,
                height: height,
                type: "HTMLNode",
                selector: ReactNativeElementSelector(
                    appcuesID: appcuesID,
                    nativeID: nil,
                    testID: nil,
                    tag: tag
                ),
                children: nil,
                displayName: appcuesID ?? tag
            )
        }
    }
}

@available(iOS 13.0, *)
internal extension Sequence {
    func asyncCompactMap<T>(_ transform: (Element) async throws -> T?) async rethrows -> [T] {
        var values = [T]()

        for element in self {
            guard let value = try await transform(element) else {
                continue
            }

            values.append(value)
        }

        return values
    }
}
