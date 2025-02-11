import type { ViewProps } from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

interface NativeProps extends ViewProps {
  frameID: string;
}

export default codegenNativeComponent<NativeProps>('AppcuesFrameView', {
  interfaceOnly: true,
});
