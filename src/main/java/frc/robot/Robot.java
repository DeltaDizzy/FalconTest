/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private final TalonFX talon = new TalonFX(1); // TODO: get device number
  //private final TalonSRX talon = new TalonSRX(7);
  private final int pidIdx = 0;
  private final int timeoutMs = 30;
  private double current_rpm;
  private double target_rpm;
  private Joystick j = new Joystick(0);
  private double forwardSoftLimitCurrent = 0.75;
  private boolean talonEnabled = true;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
    talon.configFactoryDefault();
    talon.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
    //talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

    talon.setSensorPhase(true);

    talon.configNominalOutputForward(0);
    talon.configNominalOutputReverse(0);
    talon.configPeakOutputForward(1);
    talon.configPeakOutputReverse(-1);
    talon.configOpenloopRamp(3);
 
    talon.config_kP(0,1);
    talon.config_kI(0, 0);
    talon.config_kD(0, 0);
    talon.config_kF(0, 0);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    double targetSpeed = target_rpm * 2048 / 600;
    // 40 percent - current gooes up, stops
    //double targetSpeed = target_rpm * 4096 / 600;
    SmartDashboard.putNumber("Current RPM", CalculateRPM());
    SmartDashboard.putNumber("Target RPM Readout", target_rpm);
    target_rpm = SmartDashboard.getNumber("Target RPM Input", target_rpm);
    SmartDashboard.putNumber("Current", talon.getSupplyCurrent());

    /*if(talon.getSupplyCurrent() < forwardSoftLimitCurrent && talonEnabled)
    {
      talon.set(ControlMode.PercentOutput, 0.4);
    }
    else
    {
      talon.set(ControlMode.PercentOutput, 0);
      talonEnabled = false;
    }*/

    //talon.set(ControlMode.Velocity, targetSpeed);
    talon.set(ControlMode.PercentOutput, j.getY());
  }

  public double CalculateRPM()
  {
    // get RPM
    current_rpm = talon.getSelectedSensorVelocity() / 2048 * 600; //pulsesPer100ms/pulsesPerRev => minutes
    return current_rpm;
  }


  /*
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    talon.set(ControlMode.Velocity, 100);
  }
}
