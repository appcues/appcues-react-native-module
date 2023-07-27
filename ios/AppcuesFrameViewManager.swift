import AppcuesKit

@objc(AppcuesFrameViewManager)
class AppcuesFrameViewManager: RCTViewManager {

    override func view() -> WrapperView {
        return WrapperView(uiManager: bridge.uiManager)
    }

    @objc override static func requiresMainQueueSetup() -> Bool {
        return false
    }
}

class WrapperView: UIView {
    weak var uiManager: RCTUIManager?

    @objc var frameID: String? = nil
    @objc var fixedSize: Bool = false {
        didSet {
            if let fixedSizeConstraint = fixedSizeConstraint {
                fixedSizeConstraint.isActive = fixedSize
            }
        }
    }

    private var frameView: AppcuesFrameView?
    private var fixedSizeConstraint: NSLayoutConstraint?

    init(uiManager: RCTUIManager) {
        self.uiManager = uiManager
        super.init(frame: .zero)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        // One-time setup once all the required info is available
        if frameView == nil,
           let frameID = frameID,
           let appcues = AppcuesReactNative.implementation,
           let parentViewController = parentViewController {
            let view = AppcuesFrameView()
            view.translatesAutoresizingMaskIntoConstraints = false

            addSubview(view)
            var constraints = [
                view.topAnchor.constraint(equalTo: topAnchor),
                view.leadingAnchor.constraint(equalTo: leadingAnchor),
                view.trailingAnchor.constraint(equalTo: trailingAnchor),
            ]

            // Create the constraint, but only activate it when needed. The didSet for `fixedSize` will toggle so that the setting is dynamic.
            let constraint = view.bottomAnchor.constraint(equalTo: bottomAnchor)
            fixedSizeConstraint = constraint

            if fixedSize {
                constraints.append(constraint)
            }

            NSLayoutConstraint.activate(constraints)

            appcues.register(frameID: frameID, for: view, on: parentViewController)
            frameView = view
        }

        if !fixedSize, let bounds = frameView?.bounds {
            uiManager?.setIntrinsicContentSize(bounds.size, for: self)
        }
    }
}

extension UIView {
    var parentViewController: UIViewController? {
        var parentResponder: UIResponder? = self
        while parentResponder != nil {
            parentResponder = parentResponder!.next
            if let viewController = parentResponder as? UIViewController {
                return viewController
            }
        }
        return nil
    }
}
