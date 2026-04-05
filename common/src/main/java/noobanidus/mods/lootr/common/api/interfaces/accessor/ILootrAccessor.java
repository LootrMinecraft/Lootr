package noobanidus.mods.lootr.common.api.interfaces.accessor;

public sealed interface ILootrAccessor<T> permits ILootrDataAccessor, ILootrItemFrameAccessor {
  Class<T> getAssignableClass();

  default int priority () {
    return 0;
  }
}
