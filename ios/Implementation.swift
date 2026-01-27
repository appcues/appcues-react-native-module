import AppcuesKit

@objc public protocol EventDelegate {
    func sendEvent(name: String, result: [String: Any])
}

// These typealiases are necessary so the auto-generated "-Swift" header has the right function signatures.
// Otherwise functions with @escaping closures fail to compile in the header with an "Expected a type" error.
// Copies RCTPromiseResolveBlock
public typealias ResolveBlock = (Any?) -> Void
// Copies RCTPromiseRejectBlock
public typealias RejectBlock = (String?, String?, (any Error)?) -> Void

@objc(Implementation)
public class Implementation: NSObject {
    @objc public static let eventName = "analytics"

    static var implementation: Appcues?

    private let analyticsDelegate = AnalyticsDelegate()
    @objc public weak var delegate: EventDelegate? = nil

    private var hasListeners = false

    @objc
    public func setup(
        accountID: String,
        applicationID: String,
        options: [String: Any],
        resolve: RCTPromiseResolveBlock,
        reject: RCTPromiseRejectBlock
    ) {

        // since a native module makes native calls asynchronously, we use a Promise here to allow callers to
        // be able to reliably know when initialization is complete and subsequent SDK calls can continue.
        defer { resolve(nil) }

        // Fast refreshing can result in this being called multiple times which gets weird. `guard` is a quick way to shortcut that.
        guard Implementation.implementation == nil else { return }

        let config = Appcues.Config(accountID: accountID, applicationID: applicationID)

        if let logging = options["logging"] as? Bool {
            config.logging(logging)
        }

        if let apiHost = options["apiHost"] as? String, let url = URL(string: apiHost) {
            config.apiHost(url)
        }

        if let settingsHost = options["settingsHost"] as? String, let url = URL(string: settingsHost) {
            config.settingsHost(url)
        }

        if let sessionTimeout = options["sessionTimeout"] as? UInt {
            config.sessionTimeout(sessionTimeout)
        }

        if let activityStorageMaxSize = options["activityStorageMaxSize"] as? UInt {
            config.activityStorageMaxSize(activityStorageMaxSize)
        }

        if let activityStorageMaxAge = options["activityStorageMaxAge"] as? UInt {
            config.activityStorageMaxAge(activityStorageMaxAge)
        }

        if let enableStepRecoveryObserver = options["enableStepRecoveryObserver"] as? Bool {
            config.enableStepRecoveryObserver(enableStepRecoveryObserver)
        }

        // Enable text scaling by default because the React Native Text component has scaling enabled by default.
        config.enableTextScaling(options["enableTextScaling"] as? Bool ?? true)

        let autoPropsFromOptions = options["additionalAutoProperties"] as? [String: Any] ?? [:]
        config.additionalAutoProperties(autoPropsFromOptions)

        Implementation.implementation = Appcues(config: config)

        // Implementation cannot directly conform to AppcuesAnalyticsDelegate, because then the `didTrack`
        // delegate method would need to be public, but its interface includes a Swift enum and
        // "ISO C++ forbids forward references to 'enum' types".
        analyticsDelegate.implementation = self
        Implementation.implementation?.analyticsDelegate = analyticsDelegate

        if #available(iOS 13.0, *) {
            Appcues.elementTargeting = ReactNativeElementTargeting()
        }

        handleStoredNativePush()
    }

    @objc
    public func identify(userID: String, properties: [String: Any]) {
        Implementation.implementation?.identify(userID: userID, properties: properties)
    }

    @objc
    public func reset() {
        Implementation.implementation?.reset()
    }

    @objc
    public func anonymous() {
        Implementation.implementation?.anonymous()
    }

    @objc
    public func group(groupID: String?, properties: [String: Any]) {
        Implementation.implementation?.group(groupID: groupID, properties: properties)
    }

    @objc
    public func screen(title: String, properties: [String: Any]) {
        Implementation.implementation?.screen(title: title, properties: properties)
    }

    @objc
    public func track(name: String, properties: [String: Any]) {
        Implementation.implementation?.track(name: name, properties: properties)
    }

    @objc
    public func show(
        experienceID: String,
        resolver resolve: @escaping ResolveBlock,
        rejecter reject: @escaping RejectBlock
    ) {
        Implementation.implementation?.show(experienceID: experienceID) { success, error in
            if success {
                resolve(nil)
            } else {
                reject("show-experience-failure", "unable to show experience \(experienceID)", error)
            }
        }
    }

    @objc
    public func debug() {
        DispatchQueue.main.async {
            Implementation.implementation?.debug()
        }
    }

    @objc
    public func didHandleURL(
        url: String,
        resolver resolve: @escaping ResolveBlock,
        rejecter reject: RCTPromiseRejectBlock
    ) {
        guard let url = URL(string: url) else { return resolve(false) }
        guard let implementation = Implementation.implementation else { return resolve(false) }

        DispatchQueue.main.async {
            resolve(implementation.didHandleURL(url))
        }
    }
}

extension Implementation {
    private static var pushToken: Data?
    private static var notificationResponse: UNNotificationResponse?

    @objc
    public static func setPushToken(_ deviceToken: Data?) {
        guard let impl = Implementation.implementation else {
            Implementation.pushToken = deviceToken
            return
        }

        impl.setPushToken(deviceToken)
    }

    @objc
    public static func didReceiveNotification(response: UNNotificationResponse, completionHandler: @escaping () -> Void) -> Bool {
        guard let impl = Implementation.implementation else {
            Implementation.notificationResponse = response
            return false
        }

        return impl.didReceiveNotification(response: response, completionHandler: completionHandler)
    }

    // To be called at setup
    private func handleStoredNativePush() {
        if let pushToken = Implementation.pushToken {
            Implementation.implementation?.setPushToken(pushToken)
        }
        if let notification = Implementation.notificationResponse {
            _ = Implementation.implementation?.didReceiveNotification(response: notification, completionHandler: {})
        }
    }
}

private class AnalyticsDelegate: AppcuesAnalyticsDelegate {
    weak var implementation: Implementation?

    func didTrack(analytic: AppcuesAnalytic, value: String?, properties: [String: Any]?, isInternal: Bool) {
        let analyticName: String
        switch analytic {
        case .event:
            analyticName = "EVENT"
        case .screen:
            analyticName = "SCREEN"
        case .identify:
            analyticName = "IDENTIFY"
        case .group:
            analyticName = "GROUP"
        }

        implementation?.delegate?.sendEvent(name: Implementation.eventName, result: [
            "analytic": analyticName,
            "value": value ?? "",
            "properties": properties ?? [:],
            "isInternal": isInternal
        ])
    }
}
