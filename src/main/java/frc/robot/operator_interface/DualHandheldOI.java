package frc.robot.operator_interface;

//import static frc.robot.Constants.*;

//import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;
//import edu.wpi.first.wpilibj2.command.button.Trigger;

/** Class for controlling the robot with a dual Xbox controllers. */
public class DualHandheldOI extends SingleHandheldOI {
  private final XboxController operator;

  public DualHandheldOI(int port0, int port1) {
    super(port0);
    operator = new XboxController(port1);
  }

  @Override
  public boolean testResults(int mode) {
    boolean result = true;
    if (mode == DRIVER) {
      result = super.testResults(mode);
    } else if (mode == OPERATOR) {
      testController(operator, tests[mode]);

      for (boolean test : tests[mode]) {
        result = result && test;
      }
    }
    return result;
  }
}
