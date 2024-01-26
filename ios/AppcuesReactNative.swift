import AppcuesKit

@objc(AppcuesReactNative)
class AppcuesReactNative: RCTEventEmitter {
    private static let eventName = "analytics"

    static var implementation: Appcues?

    private var hasListeners = false

    @objc
    override static func requiresMainQueueSetup() -> Bool { false }

    @objc
    func setup(_ accountID: String,
               applicationID: String,
               _ options: [String: Any],
               _ additionalAutoProperties: [String: Any],
               _ resolve: @escaping RCTPromiseResolveBlock,
               _ reject: @escaping RCTPromiseRejectBlock) {

        // since a native module makes native calls asynchronously, we use a Promise here to allow callers to
        // be able to reliably know when initialization is complete and subsequent SDK calls can continue.
        defer { resolve(nil) }

        // Fast refreshing can result in this being called multiple times which gets weird. `guard` is a quick way to shortcut that.
        guard AppcuesReactNative.implementation == nil else { return }

        let config = Appcues.Config(accountID: accountID, applicationID: applicationID)

        if let logging = options["logging"] as? Bool {
            config.logging(logging)
        }

        if let apiHost = options["apiHost"] as? String, let url = URL(string: apiHost) {
            config.apiHost(url)
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

        // Enable text scaling by default because the React Native Text component has scaling enabled by default.
        config.enableTextScaling(options["enableTextScaling"] as? Bool ?? true)

        // take any auto properties provided from the calling application, and merge with our internal
        // auto properties passed in an additional argument.
        let autoPropsFromOptions = options["additionalAutoProperties"] as? [String: Any] ?? [:]
        let mergedAutoProperties = autoPropsFromOptions.merging(additionalAutoProperties) { _, new in new }
        config.additionalAutoProperties(mergedAutoProperties)

        AppcuesReactNative.implementation = Appcues(config: config)
        AppcuesReactNative.implementation?.analyticsDelegate = self

        if #available(iOS 13.0, *) {
            Appcues.elementTargeting = ReactNativeElementTargeting()
        }
    }

    @objc
    func identify(_ userID: String, properties: [String: Any]) {
        AppcuesReactNative.implementation?.identify(userID: userID, properties: properties)
    }

    @objc
    func reset() {
        AppcuesReactNative.implementation?.reset()
    }

    @objc
    func anonymous() {
        AppcuesReactNative.implementation?.anonymous()
    }

    @objc
    func group(_ groupID: String?, properties: [String: Any]) {
        AppcuesReactNative.implementation?.group(groupID: groupID, properties: properties)
    }

    @objc
    func screen(_ title: String, properties: [String: Any]) {
        AppcuesReactNative.implementation?.screen(title: title, properties: properties)
    }

    @objc
    func track(_ name: String, properties: [String: Any]) {
        AppcuesReactNative.implementation?.track(name: name, properties: properties)
    }

    @objc
    func show(_ experienceID: String, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        AppcuesReactNative.implementation?.show(experienceID: experienceID) { success, error in
            if success {
                resolve(nil)
            } else {
                reject("show-experience-failure", "unable to show experience \(experienceID)", error)
            }
        }
    }

    @objc
    func debug() {
        DispatchQueue.main.async {
            AppcuesReactNative.implementation?.debug()
        }
    }

    @objc
    func didHandleURL(_ url: String, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        guard let url = URL(string: url) else { return resolve(false) }
        guard let implementation = AppcuesReactNative.implementation else { return resolve(false) }

        DispatchQueue.main.async {
            resolve(implementation.didHandleURL(url))
        }
    }

    // MARK: Event Emitting

    override func supportedEvents() -> [String]! {
        [Self.eventName]
    }

    override func startObserving() {
        hasListeners = true
    }

    override func stopObserving() {
        hasListeners = false
    }
}

extension AppcuesReactNative: AppcuesAnalyticsDelegate {
    func didTrack(analytic: AppcuesAnalytic, value: String?, properties: [String: Any]?, isInternal: Bool) {
        guard hasListeners else { return }

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

        sendEvent(
            withName: Self.eventName,
            body: [
                "analytic": analyticName,
                "value": value ?? "",
                "properties": properties ?? [:],
                "isInternal": isInternal
            ])
    }
}
