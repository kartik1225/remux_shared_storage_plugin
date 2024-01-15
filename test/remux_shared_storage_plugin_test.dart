import 'package:flutter_test/flutter_test.dart';
import 'package:remux_shared_storage_plugin/remux_shared_storage_plugin.dart';
import 'package:remux_shared_storage_plugin/remux_shared_storage_plugin_platform_interface.dart';
import 'package:remux_shared_storage_plugin/remux_shared_storage_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockRemuxSharedStoragePluginPlatform
    with MockPlatformInterfaceMixin{

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final RemuxSharedStoragePluginPlatform initialPlatform = RemuxSharedStoragePluginPlatform.instance;

  test('$MethodChannelRemuxSharedStoragePlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelRemuxSharedStoragePlugin>());
  });
}
