package frc.robot.commands;

import static frc.robot.Constants.*;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.WristSubsystem;

public class L1ScoringPositionCommand extends ParallelCommandGroup {

  public L1ScoringPositionCommand(ArmSubsystem arm, WristSubsystem wrist) {
    addCommands(
        new ArmToPositionCommand(arm, ScoringConstants.L1ArmPosition).withName("ArmToL1Position"),
        new WristToPositionCommand(wrist, ScoringConstants.L1WristPosition, false)
            .withName("WristToL1Position"));
  }
}
