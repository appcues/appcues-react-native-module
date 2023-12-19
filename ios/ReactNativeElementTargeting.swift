//
//  ReactNativeElementTargeting.swift
//  appcues-react-native
//
//  Created by James Ellis on 3/21/23.
//

import AppcuesKit
import UIKit

internal class ReactNativeElementSelector: AppcuesElementSelector {
    let nativeID: String?
    let testID: String?

    var displayName: String? {
        if let nativeID = nativeID {
            return nativeID
        } else if let testID = testID {
            return testID
        }

        return nil
    }

    init?(nativeID: String?, testID: String?) {
        // must have at least one identifiable property to be a valid selector
        if nativeID == nil && testID == nil {
            return nil
        }

        self.nativeID = nativeID
        self.testID = testID
        super.init()
    }

    override func evaluateMatch(for target: AppcuesElementSelector) -> Int {
        guard let target = target as? ReactNativeElementSelector else {
            return 0
        }

        // weight the selector property matches by how distinct they are considered
        var weight = 0

        if nativeID != nil && nativeID == target.nativeID {
            weight += 10_000
        }

        if testID != nil && testID == target.testID {
            weight += 1_000
        }

        return weight
    }

    private enum CodingKeys: String, CodingKey {
        case nativeID
        case testID
    }

    override func encode(to encoder: Encoder) throws {
        try super.encode(to: encoder)
        var container = encoder.container(keyedBy: CodingKeys.self)
        if let nativeID = nativeID, !nativeID.isEmpty {
            try container.encode(nativeID, forKey: .nativeID)
        }
        if let testID = testID, !testID.isEmpty {
            try container.encode(testID, forKey: .testID)
        }
    }
}

@available(iOS 13.0, *)
internal class ReactNativeElementTargeting: AppcuesElementTargeting {
    func captureLayout() -> AppcuesViewElement? {
        return UIApplication.shared.windows.first { !$0.isAppcuesWindow }?.captureLayout()
    }

    func inflateSelector(from properties: [String: String]) -> AppcuesElementSelector? {
        return ReactNativeElementSelector(
            nativeID: properties["nativeID"],
            testID: properties["testID"]
        )
    }
}

extension UIView {
    var reactNativeSelector: ReactNativeElementSelector? {
        return ReactNativeElementSelector(
            nativeID: nativeID.flatMap { $0.isEmpty ? nil : $0 },
            // on iOS, the "testID" set on a react native view comes in through
            // the accessibilityIdentifier property on the UIView
            testID: accessibilityIdentifier.flatMap { $0.isEmpty ? nil : $0 }
        )
    }

    func captureLayout() -> AppcuesViewElement? {
        return self.asCaptureView(in: self.bounds, safeAreaInsets: self.safeAreaInsets)
    }

    private func asCaptureView(in bounds: CGRect, safeAreaInsets: UIEdgeInsets) -> AppcuesViewElement? {
        let absolutePosition = self.convert(self.bounds, to: nil)

        // discard views that are not visible in the screenshot image
        guard absolutePosition.intersects(bounds) else { return nil }

        let childInsets = UIEdgeInsets(
            top: max(safeAreaInsets.top, self.safeAreaInsets.top),
            left: max(safeAreaInsets.left, self.safeAreaInsets.left),
            bottom: max(safeAreaInsets.bottom, self.safeAreaInsets.bottom),
            right: max(safeAreaInsets.right, self.safeAreaInsets.right)
        )

        let children: [AppcuesViewElement] = self.subviews.compactMap {
            // discard hidden views and subviews within
            guard !$0.isHidden else { return nil }
            return $0.asCaptureView(in: bounds, safeAreaInsets: childInsets)
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
