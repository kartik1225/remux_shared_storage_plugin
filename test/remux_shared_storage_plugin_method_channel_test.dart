import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:remux_shared_storage_plugin/remux_shared_storage_plugin_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelRemuxSharedStoragePlugin platform = MethodChannelRemuxSharedStoragePlugin();
  const MethodChannel channel = MethodChannel('remux_shared_storage_plugin');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });
}
