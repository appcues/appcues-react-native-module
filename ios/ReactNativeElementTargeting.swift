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
        return self.asCaptureView(in: self.bounds)
    }

    private func asCaptureView(in bounds: CGRect) -> AppcuesViewElement? {
        let absolutePosition = self.convert(self.bounds, to: nil)

        // discard views that are not visible in the screenshot image
        guard absolutePosition.intersects(bounds) else { return nil }

        let children: [AppcuesViewElement] = self.subviews.compactMap {
            // discard hidden views and subviews within
            guard !$0.isHidden else { return nil }
            return $0.asCaptureView(in: bounds)
        }

        return AppcuesViewElement(
            x: absolutePosition.origin.x,
            y: absolutePosition.origin.y,
            width: absolutePosition.width,
            height: absolutePosition.height,
            type: "\(type(of: self))",
            selector: reactNativeSelector,
            children: children.isEmpty ? nil : children,
            displayName: reactNativeSelector?.displayName)
    }
}
