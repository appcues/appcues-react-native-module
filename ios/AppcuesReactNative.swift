import AppcuesKit

@objc(AppcuesReactNative)
class AppcuesReactNative: NSObject {
    private var implementation: Appcues?

    @objc
    static func requiresMainQueueSetup() -> Bool { false }

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
    func show(_ experienceID: String) {
        implementation?.show(experienceID: experienceID)
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
}
