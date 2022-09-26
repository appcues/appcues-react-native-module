import AppcuesKit

@objc(AppcuesReactNative)
class AppcuesReactNative: RCTEventEmitter {
    private static let eventName = "analytics"

    private var implementation: Appcues?

    private var hasListeners = false

    @objc
    override static func requiresMainQueueSetup() -> Bool { false }

    @objc
    func setup(_ accountID: String, applicationID: String, _ options: [String: Any]) {
        // Fast refreshing can result in this being called multiple times which gets weird. `guard` is a quick way to shortcut that.
        guard implementation == nil else { return }

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

        implementation = Appcues(config: config)
        implementation?.analyticsDelegate = self
    }

    @objc
    func identify(_ userID: String, properties: [String: Any]) {
        implementation?.identify(userID: userID, properties: properties)
    }

    @objc
    func reset() {
        implementation?.reset()
    }

    @objc
    func anonymous() {
        implementation?.anonymous()
    }

    @objc
    func group(_ groupID: String, properties: [String: Any]) {
        implementation?.group(groupID: groupID, properties: properties)
    }

    @objc
    func screen(_ title: String, properties: [String: Any]) {
        implementation?.screen(title: title, properties: properties)
    }

    @objc
    func track(_ name: String, properties: [String: Any]) {
        implementation?.track(name: name, properties: properties)
    }

    @objc
    func show(_ experienceID: String, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        implementation?.show(experienceID: experienceID) { success, _ in
            resolve(success)
        }
    }

    @objc
    func debug() {
        DispatchQueue.main.async {
            self.implementation?.debug()
        }
    }

    @objc
    func didHandleURL(_ url: String, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) {
        guard let url = URL(string: url) else { return resolve(false) }
        guard let implementation = implementation else { return resolve(false) }

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
                "properties": formatProperties(properties),
                "isInternal": isInternal
            ])
    }

    /// Map any supported property types that `sendEvent` doesn't handle by default.
    private func formatProperties( _ properties: [String: Any]?) -> [String: Any] {
        guard var properties = properties else { return [:] }

        properties.forEach { key, value in
            switch value {
            case let date as Date:
                properties[key] = (date.timeIntervalSince1970 * 1000).rounded()
            case let dict as [String: Any]:
                properties[key] = formatProperties(dict)
            case let arr as [Any]:
                properties[key] = formatProperties(arr)
            default:
                break
            }
        }

        return properties
    }

    private func formatProperties( _ properties: [Any]?) -> [Any] {
        guard var properties = properties else { return [] }

        properties.enumerated().forEach { index, value in
            switch value {
            case let date as Date:
                properties[index] = (date.timeIntervalSince1970 * 1000).rounded()
            case let dict as [String: Any]:
                properties[index] = formatProperties(dict)
            case let arr as [Any]:
                properties[index] = formatProperties(arr)
            default:
                break
            }
        }

        return properties
    }
}
