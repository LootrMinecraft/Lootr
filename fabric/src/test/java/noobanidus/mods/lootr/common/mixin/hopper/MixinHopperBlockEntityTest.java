package noobanidus.mods.lootr.common.mixin.hopper;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.Level;
import noobanidus.mods.lootr.common.api.ILootrAPI;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.data.entity.ILootrEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MixinHopperBlockEntityTest {
  private static final Method PREVENT_HOPPER_ENTITY_INTERACTION = findMethod();

  private ILootrAPI previousApi;
  private ILootrAPI api;

  @BeforeAll
  static void bootstrapMinecraft() {
    SharedConstants.tryDetectVersion();
    Bootstrap.bootStrap();
  }

  @BeforeEach
  void setUp() {
    previousApi = LootrAPI.INSTANCE;
    api = mock(ILootrAPI.class);
    LootrAPI.INSTANCE = api;
  }

  @AfterEach
  void tearDown() {
    LootrAPI.INSTANCE = previousApi;
  }

  @Test
  void returnsNullResultUnchangedWithoutLootrChecks() {
    assertNull(invokeWrapper(null));

    verifyNoInteractions(api);
  }

  @Test
  void returnsNonEntityContainerUnchangedWithoutLootrChecks() {
    Container container = mock(Container.class);

    assertSame(container, invokeWrapper(container));
    verifyNoInteractions(api);
  }

  @Test
  void returnsOrdinaryEntityContainerUnchanged() {
    AbstractMinecartContainer container = entityContainer(false);

    assertSame(container, invokeWrapper(container));
    verify(api).resolveEntity(container);
  }

  @Test
  void blocksTaggedLootrContainerEntityWithoutResolvingIt() {
    AbstractMinecartContainer container = entityContainer(true);

    assertNull(invokeWrapper(container));
    verify(api, never()).resolveEntity(any());
  }

  @Test
  void blocksEntityResolvedAsLootrEntity() {
    AbstractMinecartContainer container = entityContainer(false);
    ILootrEntity resolved = mock(ILootrEntity.class);
    when(api.resolveEntity(container)).thenReturn(resolved);

    assertNull(invokeWrapper(container));
    verify(api).resolveEntity(container);
  }

  private AbstractMinecartContainer entityContainer(boolean tagged) {
    AbstractMinecartContainer container = mock(AbstractMinecartContainer.class);
    EntityType<?> entityType = mock(EntityType.class);
    doReturn(entityType).when(container).getType();
    when(entityType.is(LootrTags.Entity.CONTAINERS)).thenReturn(tagged);
    return container;
  }

  private Container invokeWrapper(Container result) {
    Operation<Container> original = args -> result;
    try {
      return (Container) PREVENT_HOPPER_ENTITY_INTERACTION.invoke(
          null, null, 0.0, 0.0, 0.0, original);
    } catch (InvocationTargetException exception) {
      if (exception.getCause() instanceof RuntimeException runtimeException) {
        throw runtimeException;
      }
      if (exception.getCause() instanceof Error error) {
        throw error;
      }
      throw new AssertionError(exception.getCause());
    } catch (ReflectiveOperationException exception) {
      throw new AssertionError(exception);
    }
  }

  private static Method findMethod() {
    try {
      Method method = MixinHopperBlockEntity.class.getDeclaredMethod(
          "lootr$preventHopperEntityInteraction",
          Level.class,
          double.class,
          double.class,
          double.class,
          Operation.class);
      method.setAccessible(true);
      return method;
    } catch (ReflectiveOperationException exception) {
      throw new ExceptionInInitializerError(exception);
    }
  }
}
