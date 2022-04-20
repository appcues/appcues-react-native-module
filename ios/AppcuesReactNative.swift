import AppcuesKit

@objc(AppcuesReactNative)
class AppcuesReactNative: NSObject {
    private var implementation: Appcues?

    @objc
    static func requiresMainQueueSetup() -> Bool { false }

    @objc
    func setup(_ accountID: String, applicationID: String) {
        // Fast refreshing can result in this being called multiple times which gets weird. `guard` is a quick way to shortcut that.
        guard implementation == nil else { return }

        let config = Appcues.Config(accountID: accountID, applicationID: applicationID).logging(true)
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
}
