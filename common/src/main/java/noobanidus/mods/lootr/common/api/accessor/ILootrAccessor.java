package noobanidus.mods.lootr.common.api.accessor;

public sealed interface ILootrAccessor<T> permits ILootrDataAccessor, ILootrItemFrameAccessor {
  Class<T> getAssignableClass();

  default int priority () {
    return 0;
  }
}
