package frc.robot;

import static frc.robot.Constants.*;

// import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathfindingCommand;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
// import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
// import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
// import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
// import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.commands.*;
import frc.robot.generated.TunerConstants;
import frc.robot.operator_interface.OISelector;
import frc.robot.operator_interface.OperatorInterface;
import frc.robot.subsystems.*;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  private static RobotContainer instance;

  /* Setting up bindings for necessary control of the swerve drive platform */
  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
  // private final SwerveRequest.RobotCentric forwardStraight =
  //     new SwerveRequest.RobotCentric().withDriveRequestType(DriveRequestType.OpenLoopVoltage);

  private final Telemetry logger = new Telemetry(DriveTrainConstants.maxSpeed);

  /* Operator Interface */
  public OperatorInterface oi = new OperatorInterface() {};

  /* Subsystems */
  public final PowerDistribution power = new PowerDistribution();
  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
  public final VisionSubsystem vision = new VisionSubsystem();
  public final ArmSubsystem arm = new ArmSubsystem();
  public final WristSubsystem wrist = new WristSubsystem();
  public final IntakeSubsystem intake = new IntakeSubsystem();
  // public final ClimberSubsystem climber = new ClimberSubsystem();

  /* Path follower */
  private SendableChooser<Command> autoChooser;

  public static RobotContainer GetInstance() {
    return instance;
  }

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {

    instance = this;

    // disable all telemetry in the LiveWindow to reduce the processing during each iteration
    LiveWindow.disableAllTelemetry();

    drivetrain.setMaxSpeeds(DriveTrainConstants.maxSpeed, DriveTrainConstants.maxAngularRate);
    configureAutoPaths();
    configureAutoCommands();
    configureTelemetry();
  }

  /**
   * This method scans for any changes to the connected joystick. If anything changed, it creates
   * new OI objects and binds all of the buttons to commands.
   */
  public void updateOI() {
    if (!OISelector.didJoysticksChange()) {
      SmartDashboard.putNumber("DriveTrain/Drive Scaling", oi.driveScalingValue());
      return;
    }

    CommandScheduler.getInstance().getActiveButtonLoop().clear();
    oi = OISelector.findOperatorInterface();

    configureButtonBindings();
    configureDefaultCommands();
  }

  private void configureButtonBindings() {
    // reset gyro to 0 degrees
    oi.getResetGyroButton().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

    // x-stance
    oi.getXStanceButton().whileTrue(drivetrain.applyRequest(() -> brake));

    oi.driveScalingUp().onTrue(Commands.runOnce(() -> drivetrain.setMaxSpeeds(DriveTrainConstants.maxSpeed, DriveTrainConstants.maxAngularRate)));
    oi.driveScalingDown().onTrue(Commands.runOnce(() -> drivetrain.setMaxSpeeds(DriveTrainConstants.maxSpeed * 0.6, DriveTrainConstants.maxAngularRate * 0.6)));
    oi.driveScalingSlow().onTrue(Commands.runOnce(() -> drivetrain.setMaxSpeeds(DriveTrainConstants.maxSpeed * 0.1, DriveTrainConstants.maxAngularRate * 0.2)))
                         .onFalse(Commands.runOnce(() -> drivetrain.setMaxSpeeds(DriveTrainConstants.maxSpeed * 0.6, DriveTrainConstants.maxAngularRate * 0.6)));

    // // Run SysId routines when holding back/start and X/Y.
    // // Note that each routine should be run exactly once in a single log.
    // oi.getStartButton()
    //     .and(oi.getYButton())
    //     .whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
    // oi.getStartButton()
    //     .and(oi.getXButton())
    //     .whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));
    // oi.getBackButton().and(oi.getYButton()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
    // oi.getBackButton().and(oi.getXButton()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));

    // // Run test routines (forward/back at .5 m/s) when holding start and A/B.
    // oi.getStartButton()
    //     .and(oi.getAButton())
    //     .whileTrue(
    //         drivetrain.applyRequest(() -> forwardStraight.withVelocityX(0.5).withVelocityY(0)));
    // oi.getStartButton()
    //     .and(oi.getBButton())
    //     .whileTrue(
    //         drivetrain.applyRequest(() -> forwardStraight.withVelocityX(-0.5).withVelocityY(0)));

    // // Run test pose routines when holding back and A/B.
    // oi.getBackButton()
    //     .and(oi.getAButton())
    //     .whileTrue(
    //         new PathOnTheFlyCommand(
    //             drivetrain, new Pose2d(16.24, 0.8, Rotation2d.fromDegrees(-60))));
    // oi.getBackButton()
    //     .and(oi.getBButton())
    //     .whileTrue(
    //         new PathOnTheFlyCommand(
    //             drivetrain, new Pose2d(13.85, 2.67, Rotation2d.fromDegrees(124))));

    oi.getAButton().onTrue(new IntakeCommand(intake));
    oi.getXButton().onTrue(new OuttakeCommand(intake));
    oi.getBButton().onTrue(new OuttakeSpinCommand(intake));
    oi.getYButton().onTrue(new CruisePositionCommand(arm, wrist));

    oi.getPOVUp().onTrue(new GetCoralPositionCommand(arm, wrist));
    oi.getPOVDown().onTrue(new ClimbPositionCommand(arm, wrist));
    oi.getPOVLeft().onTrue(new L1ScoringPositionCommand(arm, wrist));
    oi.getPOVRight().onTrue(new L2ScoringPositionCommand(arm, wrist));

    // oi.getLeftBumper().onTrue(Commands.runOnce(() -> climber.setClamp(false)));
    // oi.getRightBumper().onTrue(Commands.runOnce(() -> climber.setClamp(true)));

    // oi.getLeftTrigger().onTrue(Commands.runOnce(() -> climber.rotateOpen()));
    // oi.getRightTrigger().onTrue(Commands.runOnce(() -> climber.rotateClosed()));
    // // oi.getLeftTrigger().onTrue(Commands.runOnce(() -> climber.rotateClosed()));
    // // oi.getRightTrigger().onTrue(Commands.runOnce(() -> climber.climb()));

    // oi.getStartButton().onTrue(Commands.runOnce(() -> climber.resetMotorPostion()));
    // oi.getBackButton().onTrue(Commands.runOnce(() -> climber.rotateOpen()));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }

  /** Use this method to define your commands for autonomous mode. */
  private void configureAutoCommands() {
    // Add commands to Autonomous Sendable Chooser
    autoChooser = AutoBuilder.buildAutoChooser("Forward");

    PathfindingCommand.warmupCommand().schedule();
  }

  private void configureDefaultCommands() {
    drivetrain.setDefaultCommand(new SwerveTeleopCommand(drivetrain, oi));

    arm.setDefaultCommand(Commands.run(() -> arm.teleop(-oi.getLeftThumbstickY()), arm));
    wrist.setDefaultCommand(Commands.run(() -> wrist.teleop(oi.getLeftThumbstickX()), wrist));
    // climber.setDefaultCommand(
    //     Commands.run(
    //         () -> { climber.teleopClimb(-oi.getRightThumbstickY()); climber.teleopRotate(oi.getRightThumbstickX()); }, climber));
    vision.setDefaultCommand(new PhotonVisionCommand(vision, drivetrain));
  }

  private void configureAutoPaths() {
    NamedCommands.registerCommand("Intake", new IntakeCommand(intake));
    NamedCommands.registerCommand("Outtake", new OuttakeCommand(intake));
    NamedCommands.registerCommand("OuttakeSpin", new OuttakeSpinCommand(intake));
    NamedCommands.registerCommand("SetClimbPos", new ClimbPositionCommand(arm, wrist));
    NamedCommands.registerCommand("SetCruisePos", new CruisePositionCommand(arm, wrist));
    NamedCommands.registerCommand("GetCoral", new GetCoralPositionCommand(arm, wrist));
    NamedCommands.registerCommand("SetL1Score", new L1ScoringPositionCommand(arm, wrist));
    NamedCommands.registerCommand("SetL2Score", new L2ScoringPositionCommand(arm, wrist));
    NamedCommands.registerCommand("DoL2Score", new L2ScoringCommand(this));
  }

  private void configureTelemetry() {
    drivetrain.registerTelemetry(logger::telemeterize);

    SmartDashboard.putData("AutoChooser", autoChooser);
    SmartDashboard.putData("Intake", new IntakeCommand(intake));
    SmartDashboard.putData("Outtake", new OuttakeCommand(intake));
    SmartDashboard.putData("OuttakeSpin", new OuttakeSpinCommand(intake));
    SmartDashboard.putData("GetCoral", new GetCoralPositionCommand(arm, wrist));
    SmartDashboard.putData("SetClimbPos", new ClimbPositionCommand(arm, wrist));
    SmartDashboard.putData("SetCruisePos", new CruisePositionCommand(arm, wrist));
    SmartDashboard.putData("SetL1Score", new L1ScoringPositionCommand(arm, wrist));
    SmartDashboard.putData("SetL2Score", new L2ScoringPositionCommand(arm, wrist));
    SmartDashboard.putData("L2Backup", new L2ScoringBackUpCommand(drivetrain));
    SmartDashboard.putData("DoL2Score", new L2ScoringCommand(this));
    // SmartDashboard.putData("ClimbToFull", new ClimbCommand(climber));
    // SmartDashboard.putData("ClimbReset", new ClimberResetCommand(climber));
    // SmartDashboard.putData("ClimbRotateOpen", new ClimberRotateOpenCommand(climber));
    // SmartDashboard.putData("ClimbRotateClosed", new ClimberRotateClosedCommand(climber));
    // SmartDashboard.putData("ResyncClimberPos", Commands.runOnce(() -> climber.resetMotorPostion(), climber));

    // ShuffleboardTab tab = Shuffleboard.getTab("MAIN");
    // tab.add("AutoChooser", autoChooser).withSize(2, 1).withWidget(BuiltInWidgets.kComboBoxChooser);
    // tab.addNumber("DriveTrain/Drive Scaling", () -> oi.driveScalingValue());
  }

  public void simulationInit() {}

  public void simulationPeriodic() {

    // Update camera simulation
    vision.simulationPeriodic(drivetrain.getState().Pose);

    var debugField = vision.getSimDebugField();
    debugField.getObject("EstimatedRobot").setPose(drivetrain.getState().Pose);
    debugField.getObject("EstimatedRobotModules").setPoses(drivetrain.getModulePoses());

    // // Calculate battery voltage sag due to current draw
    // var batteryVoltage =
    //         BatterySim.calculateDefaultBatteryLoadedVoltage(drivetrain.getCurrentDraw());
    // Using max(0.1, voltage) here isn't a *physically correct* solution,
    // but it avoids problems with battery voltage measuring 0.
    RoboRioSim.setVInVoltage(Math.max(0.1, RobotController.getBatteryVoltage()));
  }

  public void testInit() {}

  public void testPeriodic() {}

  public void testExit() {}
}
