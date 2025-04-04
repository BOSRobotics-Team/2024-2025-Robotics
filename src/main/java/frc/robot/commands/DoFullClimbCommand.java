package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.ClimberSubsystem;
import frc.robot.subsystems.WristSubsystem;

public class DoFullClimbCommand extends SequentialCommandGroup {
  /**
   * Sets arm position and climbs
   * @param climb Subsystem
   * @param arm Subsystem
   * @param wrist Subsystem
   */
  public DoFullClimbCommand(ClimberSubsystem climb, ArmSubsystem arm, WristSubsystem wrist) {
    addCommands(
        new ClimbPositionCommand(arm, wrist)
            .withName("ClimbPositionCommand"),
        new ClimbCommand(climb)
            .withName("ClimbCommand"));
  }
}
