//
//  ReactNativeElementTargeting.swift
//  appcues-react-native
//
//  Created by James Ellis on 3/21/23.
//

import AppcuesKit
import UIKit

internal class ReactNativeElementSelector: AppcuesElementSelector {
    let nativeID: String

    init?(nativeID: String?) {
        // must have at least one identifiable property to be a valid selector
        guard let nativeID = nativeID else {
            return nil
        }

        self.nativeID = nativeID
        super.init()
    }

    override func evaluateMatch(for target: AppcuesElementSelector) -> Int {
        guard let target = target as? ReactNativeElementSelector else {
            return 0
        }

        return nativeID == target.nativeID ? 10_000 : 0
    }
}

@available(iOS 13.0, *)
internal class ReactNativeElementTargeting: AppcuesElementTargeting {
    func captureLayout() -> AppcuesViewElement? {
        return UIApplication.shared.windows.first { !$0.isAppcuesWindow }?.captureLayout()
    }

    func inflateSelector(from properties: [String: String]) -> AppcuesElementSelector? {
        return ReactNativeElementSelector(nativeID: properties["nativeID"])
    }
}

extension UIView {
    var reactNativeSelector: ReactNativeElementSelector? {
        return ReactNativeElementSelector(
            nativeID: nativeID
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
            displayName: reactNativeSelector?.nativeID)
    }
}
