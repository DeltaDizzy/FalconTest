/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;

/**
 * Various methods to make configuring CTRE Motor Controllers (Talon SRX and FX)
 * easier.
 */
public class MotorConfig
{

    static TalonFXConfiguration shooterFlywheel;

    public static TalonFX CreateFalcon(int DeviceID)
    {
        TalonFX falcon = new TalonFX(DeviceID);
        // reset settings
        falcon.configFactoryDefault();
        // use builtin encoder
        falcon.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        // we want to shoot balls when going positive
        falcon.setSensorPhase(true);

        
        falcon.configNominalOutputForward(0);
        falcon.configNominalOutputReverse(0);
        falcon.configPeakOutputForward(1);
        falcon.configPeakOutputReverse(-1);

        // PID parameters
        falcon.config_kP(0,1);
        falcon.config_kI(0, 0);
        falcon.config_kD(0, 0);
        falcon.config_kF(0, 0);

        // 3-second accceleration period to preserve the motor and belts
        falcon.configOpenloopRamp(3);
        

        return falcon;
    }
}
