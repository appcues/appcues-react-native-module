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
    @objc var frameID: String? = nil {
        didSet {
            // Handle a change in value after setup
            if let frameID = frameID, let frameVC = frameViewController {
                AppcuesReactNative.implementation?.register(frameID: frameID, for: frameVC.frameView, on: frameVC)
            }
        }
    }

    private weak var uiManager: RCTUIManager?
    private weak var frameViewController: AppcuesFrameVC?

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

        if let frameViewController = frameViewController {
            frameViewController.view.frame = bounds
        } else {
            setupFrame()
        }
    }

    private func setupFrame() {
        guard let parentVC = parentViewController,
              let frameID = frameID,
              let appcues = AppcuesReactNative.implementation else {
            return
        }

        let frameVC = AppcuesFrameVC(parentView: self)
        parentVC.addChild(frameVC)
        addSubview(frameVC.view)
        frameVC.view.frame = bounds
        frameVC.didMove(toParent: parentVC)
        self.frameViewController = frameVC

        appcues.register(frameID: frameID, for: frameVC.frameView, on: frameVC)
    }

    func setIntrinsicSize(preferredContentSize: CGSize, isHidden: Bool) {
        let size: CGSize

        if isHidden {
            // When the frame is hidden, size this WrapperView to 0.
            size = .zero
        } else {
            if preferredContentSize == .zero {
                // When the frame is NOT hidden and the current size is 0, set a non-zero height,
                // which allows the content to start its layout algorithm and determine the required size.
                size = CGSize(width: 0, height: 1)
            } else {
                // When the frame is NOT hidden use the size calculated by the content.
                size = preferredContentSize
            }
        }

        uiManager?.setIntrinsicContentSize(size, for: self)
    }
}

class AppcuesFrameVC: UIViewController {
    lazy var frameView = AppcuesFrameView()

    weak var parentView: WrapperView?

    init(parentView: WrapperView) {
        self.parentView = parentView
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func loadView() {
        view = frameView
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()

        parentView?.setIntrinsicSize(preferredContentSize: preferredContentSize, isHidden: frameView.isHidden)
    }

    override func preferredContentSizeDidChange(forChildContentContainer container: UIContentContainer) {
        super.preferredContentSizeDidChange(forChildContentContainer: container)
        preferredContentSize = container.preferredContentSize

        parentView?.setIntrinsicSize(preferredContentSize: preferredContentSize, isHidden: frameView.isHidden)
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
