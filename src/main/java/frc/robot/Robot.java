/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

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
  private TalonFX falcon;
  private final TalonSRX talon = new TalonSRX(7);
  private double current_rpm;
  private double target_rpm;
  private double forwardSoftLimitCurrent = 0.75;
  private Joystick j = new Joystick(0);
  private boolean talonEnabled = true;
  private final Timer timer = new Timer();
  private final Timer elapsedTime = new Timer();

  ShuffleboardTab tab = Shuffleboard.getTab("SmartDashboard");
  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() 
  {
    falcon = MotorConfig.CreateFalcon(1);
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
  public void robotPeriodic() 
  {
    
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() 
  {
    FalconRPM();
    //TalonSoftLimit();
  }

  public void TalonSoftLimit()
  {
    double systemTime = timer.getFPGATimestamp();


    //40 percent - current gooes up, stops
    //check if current is less than 0.75A
    if(talon.getSupplyCurrent() < forwardSoftLimitCurrent && talonEnabled)
    {
      //start elapsed time
      elapsedTime.start();
    }
    if ((talon.getSupplyCurrent() >= forwardSoftLimitCurrent && talonEnabled)) 
    {
      // if current exceeds the limit
      elapsedTime.stop();
    }
    if (elapsedTime.get() > 0.5) 
    {
      talonEnabled = false;  
    }

    if(talonEnabled)
    {
      talon.set(ControlMode.PercentOutput, 0.4);
    }
    else
    {
      talon.set(ControlMode.PercentOutput, 0);
    }
  }

  public void FalconRPM()
  {
    // post current RPM to Shuffleboard
    //tab.add("Current RPM", CalculateRPM()).withWidget(BuiltInWidgets.kTextView).getEntry();
    System.out.println("Current RPM " + CalculateRPM());
    System.out.println("Encoder Ticks " + falcon.getSelectedSensorVelocity());
    //NetworkTableEntry targetRPM = tab.add("Target RPM", 0).withWidget(BuiltInWidgets.kTextView).withProperties(Map.of("min", 0, "max", 6000)).getEntry();
    
    // read target RPM from Shuffleboard
    //target_rpm = targetRPM.getDouble(0.0);
    // convert target RPM to encoder units
    //double targetSpeed = target_rpm * 4096 / 600;

    falcon.set(ControlMode.PercentOutput, j.getY());
  }

  public double CalculateRPM()
  {
    // get RPM
    current_rpm = falcon.getSelectedSensorVelocity() / 2048 * 600; //pulsesPer100ms/pulsesPerRev => minutes
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
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    falcon.set(ControlMode.Velocity, 100);
  }
}
