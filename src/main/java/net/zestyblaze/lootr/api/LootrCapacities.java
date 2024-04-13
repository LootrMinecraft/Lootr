package net.zestyblaze.lootr.api;

public interface LootrCapacities {
  int VERSION = 3;

  // Available from: 1
  // Description: The initial capacities command.
  // Provides: Nothing.
  String CAPACITIES = "capacities";
  // Available from: 1
  // Description: A flag to dictate if a structure is being saved and whether
  // or not the id and openers should be discarded during this save.
  // Provides: ILootrAPI::isSavingStructure
  String STRUCTURE_SAVING = "structure_saving";
  // Available from: 2
  // Description: Identical to structure_saving but for more general purposes
  // such as during getCloneItemStack.
  // Provides: ILootrAPI::shouldDiscard
  String SHOULD_DISCARD = "should_discard_id_and_openers";
  // Available from: 3
  // Description: Returns a float value for the explosion resistance of a block
  // depending on the configuration.
  // Provides: ILootrAPI::getExplosionResistance
  String EXPLOSION_RESISTANCE = "explosion_resistance";
  // Available from: 3
  // Description: Returns a float value for the destruction progress of a block
  // depending on configuration.
  // Provides: ILootrAPI::getDestructionProgress
  String DESTRUCTION_PROGRESS = "destruction_progress";

}
