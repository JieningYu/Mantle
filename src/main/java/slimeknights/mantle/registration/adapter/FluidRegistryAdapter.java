package slimeknights.mantle.registration.adapter;

import net.minecraft.core.Registry;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.registration.DelayedSupplier;
import slimeknights.mantle.registration.FluidBuilder;

import java.util.function.Function;

/**
 * Registry adapter for registering fluids
 */
@SuppressWarnings("unused")
public class FluidRegistryAdapter extends RegistryAdapter<Fluid> {
  /** @inheritDoc */
  public FluidRegistryAdapter() {
    super(Registry.FLUID, Mantle.modId);
  }

  /** @inheritDoc */
  public FluidRegistryAdapter(String modId) {
    super(Registry.FLUID, modId);
  }

//  /**
//   * Registers a new fluid with both still and flowing variants
//   * @param builder   Fluid properties builder
//   * @param still     Still constructor
//   * @param flowing   Flowing constructor
//   * @param name      Fluid name
//   * @param <F>       Fluid type
//   * @return  Still fluid instance
//   */
//  public <F extends ForgeFlowingFluid> F register(FluidBuilder builder, Function<Properties, F> still, Function<Properties,F> flowing, String name) {
//    // have to create still and flowing later, as the props need these suppliers
//    DelayedSupplier<Fluid> stillDelayed = new DelayedSupplier<>();
//    DelayedSupplier<Fluid> flowingDelayed = new DelayedSupplier<>();
//
//    // create props with the suppliers
//    Properties props = builder.build(stillDelayed, flowingDelayed);
//
//    // create fluids now that we have props
//    F fluid = register(still.apply(props), name);
//    stillDelayed.setSupplier(fluid.delegate);
//    flowingDelayed.setSupplier(register(flowing.apply(props), "flowing_" + name).delegate);
//
//    // return the final nice object
//    return fluid;
//  }
//
//  /**
//   * Registers a fluid using default constructors
//   * @param builder  Fluid builder
//   * @param name     Fluid name
//   * @return  Still fluid
//   */
//  public ForgeFlowingFluid register(FluidBuilder builder, String name) {
//    return register(builder, ForgeFlowingFluid.Source::new, ForgeFlowingFluid.Flowing::new, name);
//  }
}
