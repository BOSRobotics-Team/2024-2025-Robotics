package frc.robot.commands;

import static frc.robot.Constants.*;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.WristSubsystem;

public class L1ScoringCommand extends SequentialCommandGroup {

  public L1ScoringCommand(ArmSubsystem arm, WristSubsystem wrist) {
    addCommands(
        new ArmToPositionCommand(arm, ScoringConstants.L1ArmPosition).withName("ArmToL1Position"),
        new WristToPositionCommand(wrist, ScoringConstants.L1WristPosition)
            .withName("WristToL1Position"));
  }
}
