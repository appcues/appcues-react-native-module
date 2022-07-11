# replace placeholders
sed -i '' -e "s/APPCUES_ACCOUNT_ID/$1/g" ../../App.js
sed -i '' -e "s/APPCUES_APPLICATION_ID/$2/g" ../../App.js
sed -i '' -e "s/APPCUES_APPLICATION_ID/$2/g" ../AppcuesReactNativeExample/Info.plist
