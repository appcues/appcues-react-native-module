# replace placeholders
if [[ "$OSTYPE" == "darwin"* ]]; then
  sed -i '' -e "s/APPCUES_ACCOUNT_ID/$1/g" ../../App.js
  sed -i '' -e "s/APPCUES_APPLICATION_ID/$2/g" ../../App.js
  sed -i '' -e "s/APPCUES_APPLICATION_ID/$2/g" ../app/src/main/AndroidManifest.xml
else
  sed -i -e "s/APPCUES_ACCOUNT_ID/$1/g" ../../App.js
  sed -i -e "s/APPCUES_APPLICATION_ID/$2/g" ../../App.js
  sed -i -e "s/APPCUES_APPLICATION_ID/$2/g" ../app/src/main/AndroidManifest.xml
fi
