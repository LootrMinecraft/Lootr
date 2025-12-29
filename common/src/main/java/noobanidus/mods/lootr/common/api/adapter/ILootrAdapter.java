package noobanidus.mods.lootr.common.api.adapter;

public sealed interface ILootrAdapter<T> permits ILootrDataAdapter, ILootrItemFrameAdapter {
  Class<T> getAssignableClass();

  default int priority () {
    return 0;
  }
}
