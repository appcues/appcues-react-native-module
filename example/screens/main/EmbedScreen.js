import React from 'react';
import { SafeAreaView, StyleSheet, ScrollView, View } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import * as Appcues from '@appcues/react-native';
import { AppcuesFrameView } from '@appcues/react-native';
import Text from '../../components/Text';

const Stack = createNativeStackNavigator();

const EmbedView = () => {
  useFocusEffect(
    React.useCallback(() => {
      Appcues.screen('Embed Container');
    }, [])
  );

  return (
    <SafeAreaView>
      <ScrollView>
        <AppcuesFrameView frameID="frame1" />
        <View style={styles.textContainer}>
          <Text style={styles.text}>
            Embedded Experiences, or Embeds for short, are experiences that are
            injected inline with the customer application views, rather than
            overlaid on top. Embeds can contain a variety of content. Any type
            of experience content you could show in a modal step could also be
            embedded into a non-modal view in the application. This pattern is
            commonly used for less obtrusive promotions or supplemental content,
            inline banners or help tips.
          </Text>
        </View>
        <AppcuesFrameView frameID="frame2" />
        <View style={styles.textContainer}>
          <Text>
            Embeds are a low-code pattern, requiring customer application
            development work to be done to create and expose injectable view
            where Appcues content can reside. This involves creating and
            registering a special AppcuesFrame view types with the iOS and
            including those views in the customer app layouts. This concept of
            view embedding is analogous to products like the Google Ads SDKs for
            mobile display ads. The details of how the embed registered with the
            native SDKs are outside of the scope of this document, which focuses
            on the data model updates. SDK documentation will cover those other
            integration topics.
          </Text>
        </View>
        <AppcuesFrameView frameID="frame3" />
        <View style={styles.textContainer}>
          <Text>
            Before mobile embeds were supported, the Appcues mobile SDKs could
            render a single mobile flow at a time, modally as an overlay. These
            experiences could have had modal or tooltip steps, but the response
            from the server qualification would return a prioritized list of
            mobile flows and the SDK would render the highest priority item
            possible. With embeds, this paradigm changes, and a qualification
            response may contain zero or more mobile embeds that can be rendered
            simultaneously, as well as zero or more mobile flows, which are
            handled just like before, rendering a single highest priority item.
            Mobile embeds and mobile flow pattern types are entirely mutually
            exclusive, meaning you cannot have tooltip mobile flow steps within
            an embed experience, for example. However, you can launch a mobile
            flow from a mobile embed with a button action.
          </Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default function EmbedScreen() {
  return (
    <Stack.Navigator>
      <Stack.Screen
        name="Embed Experiences"
        component={EmbedView}
        options={{
          headerShadowVisible: false,
          headerTitleStyle: { fontWeight: '600' },
        }}
      />
    </Stack.Navigator>
  );
}

const styles = StyleSheet.create({
  textContainer: {
    flex: 1,
    marginHorizontal: 20,
    marginBottom: 12,
    flexDirection: 'row',
  },
});
