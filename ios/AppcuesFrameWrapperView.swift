import UIKit
import AppcuesKit

@objc
public protocol WrapperSizeDelegate {
    func setSize(to size: CGSize)
}

@objc(AppcuesFrameWrapperView)
public class AppcuesFrameWrapperView: UIView {
    @objc public var frameID: String? = nil {
        didSet {
            // Handle a change in value after setup
            if let frameID = frameID, let frameVC = frameViewController {
                Implementation.implementation?.register(frameID: frameID, for: frameVC.frameView, on: frameVC)
            }
        }
    }

    @objc
    public weak var delegate: WrapperSizeDelegate?
    private weak var uiManager: RCTUIManager?
    private weak var frameViewController: AppcuesFrameVC?

    @objc
    public init(uiManager: RCTUIManager?) {
        self.uiManager = uiManager
        super.init(frame: .zero)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    public override func layoutSubviews() {
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
              let appcues = Implementation.implementation else {
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
        let size = isHidden ? .zero : preferredContentSize

        // New Arch
        delegate?.setSize(to: size)

        // Old Arch
        uiManager?.setIntrinsicContentSize(size, for: self)
    }
}

class AppcuesFrameVC: UIViewController {
    lazy var frameView = AppcuesFrameView()

    weak var parentView: AppcuesFrameWrapperView?

    init(parentView: AppcuesFrameWrapperView) {
        self.parentView = parentView
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func loadView() {
        view = frameView
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        // Only want to render the margins specified by the embed style
        viewRespectsSystemMinimumLayoutMargins = false
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()

        parentView?.setIntrinsicSize(preferredContentSize: preferredContentSize, isHidden: frameView.isHidden)
    }

    override func preferredContentSizeDidChange(forChildContentContainer container: UIContentContainer) {
        super.preferredContentSizeDidChange(forChildContentContainer: container)

        // Add frame margins to the calculated size. Need to do this because the margins must be set on the FrameView,
        // not the UIViewController it contains which manages the preferredContentSize
        let margins = frameView.directionalLayoutMargins

        preferredContentSize = CGSize(
            width: container.preferredContentSize.width + margins.leading + margins.trailing,
            height: container.preferredContentSize.height + margins.top + margins.bottom
        )

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
