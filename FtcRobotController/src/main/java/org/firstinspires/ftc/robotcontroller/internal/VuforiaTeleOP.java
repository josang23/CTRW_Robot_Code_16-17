package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by FTC on 9/24/2016.
 */

public class VuforiaTeleOP extends LinearOpMode {
    //@Override
    public DcMotor lfmotor;
    public DcMotor rfmotor;
    public DcMotor lbmotor;
    public DcMotor rbmotor;
    public DcMotor capture;
    public DcMotor shooterleft;
    public DcMotor shooterright;
//    public DcMotor conveyor;
    float drivespeed = 0;
    float turnspeed = 0;
    float slidespeed = 0;
    float shootspeed = 0;
    boolean activatecapture = false;

// test to make sure it can push/pull correctly

    public void Control()
    {
//        boolean activateconvey = gamepad1.y
        activatecapture = gamepad1.b;
        drivespeed = -gamepad1.left_stick_y;
        turnspeed = -gamepad1.right_stick_x;
        slidespeed = gamepad1.left_stick_x;
        shootspeed = gamepad1.right_trigger;

        if (drivespeed == 0 && turnspeed == 0 && slidespeed == 0){ // Stay still
        lbmotor.setPower(0);
        lfmotor.setPower(0);
        rfmotor.setPower(0);
        rbmotor.setPower(0);
    }

        else if (drivespeed != 0 && turnspeed != 0){ // Stop when trying to turn and drive
            lbmotor.setPower(0);
            lfmotor.setPower(0);
            rbmotor.setPower(0);
            rfmotor.setPower(0);
        }
        else if (slidespeed != 0 && turnspeed != 0){ // Stop when trying to turn and slide
            lbmotor.setPower(0);
            lfmotor.setPower(0);
            rbmotor.setPower(0);
            rfmotor.setPower(0);
        }

        else if (slidespeed != 0 && Math.abs(drivespeed) < .1) { // Slide
            lbmotor.setPower (slidespeed);
            lfmotor.setPower (-slidespeed);
            rbmotor.setPower(-slidespeed);
            rfmotor.setPower (slidespeed);
        }
        else if(drivespeed != 0 && Math.abs(slidespeed) < .1){  // Drive
            lbmotor.setPower(-drivespeed);
            lfmotor.setPower(-drivespeed);
            rfmotor.setPower(-drivespeed);
            rbmotor.setPower(-drivespeed);
        }
        else if(drivespeed == 0 && turnspeed > 0){ // Turn right
                lfmotor.setPower(.75 * Math.abs(turnspeed));
                lbmotor.setPower(.75 * Math.abs(turnspeed));
                rfmotor.setPower(.75 * -Math.abs(turnspeed));
                rbmotor.setPower(.75 * -Math.abs(turnspeed));
            }
        else if(drivespeed == 0 && turnspeed < 0){  // Turn left
            lfmotor.setPower(.75 * -Math.abs(turnspeed));
            lbmotor.setPower(.75 * -Math.abs(turnspeed));
            rfmotor.setPower(.75 * Math.abs(turnspeed));
            rbmotor.setPower(.75 * Math.abs(turnspeed));
        }


        if (shootspeed != 0) { // Shooting
            shooterleft.setPower(shootspeed);
            shooterright.setPower(shootspeed);
        }
        else if (shootspeed == 0) { // If not shooting do nothing
            shooterleft.setPower(0);
            shooterright.setPower(0);
        }
        if (activatecapture == true){ // Capture
            capture.setPower(-1);
        }
        else if (activatecapture == false){  // If not capturing do nothing
            capture.setPower(0);
        }


//        else if (activateconvey == true){
//            conveyor.setPower(1);
//        }
//        else if (activateconvey == false){
//            conveyor.setPower(0);
//        }




        telemetry.addData("Drive Speed: ",drivespeed);
        telemetry.addData("Turn Speed: ", turnspeed);
        telemetry.addData("Shooting Speed", shootspeed);
        telemetry.addData("Capturing:", activatecapture);
        telemetry.addData("Slidespeed:", slidespeed);





    }

    public void runOpMode() throws InterruptedException{
        lfmotor = hardwareMap.dcMotor.get("lfmotor");
        rfmotor = hardwareMap.dcMotor.get("rfmotor");
        lbmotor = hardwareMap.dcMotor.get("lbmotor");
        rbmotor = hardwareMap.dcMotor.get("rbmotor");
        capture = hardwareMap.dcMotor.get("capture");
        shooterright = hardwareMap.dcMotor.get("shooterright");
        shooterleft = hardwareMap.dcMotor.get("shooterleft");
//        conveyor = hardwareMap.dcMotor.get("conveyor");


        lbmotor.setDirection(DcMotor.Direction.FORWARD);
        lfmotor.setDirection(DcMotor.Direction.FORWARD);
        rfmotor.setDirection(DcMotor.Direction.REVERSE);
        rbmotor.setDirection(DcMotor.Direction.REVERSE);
        shooterleft.setDirection(DcMotor.Direction.FORWARD);
        shooterright.setDirection(DcMotor.Direction.REVERSE);

        lfmotor.setPower(0);
        rfmotor.setPower(0);
        lbmotor.setPower(0);
        rbmotor.setPower(0);
        shooterright.setPower(0);
        shooterleft.setPower(0);
        capture.setPower(0);
        boolean tleft = false;
        boolean tright = false;

        boolean isStoppedAtAngle = false;
        boolean targetVisible = false;
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = "AeY4MXz/////AAAAGRwm5YCC8EWCrB4+iRLrzaM6G1mJxH3xoYHxnJ+Wqu84iHxbyjnWrk2U7zlOW9dqhe9ikLeiu6edQRBE6KsPn4zHkUP25Hj+5GbJ/g0wG6RiAXjxGw7JkIsmD7rgnrMHTCrClASjmhoEUopIwc913EuQa1a2IOwgbyY9x/8SkeHsg0uzyoYWMjjh/j2m4WXDBXidBKbmO3xsjl9w8XQ4JHsL5ggcKuAdjmSwvPXFuZeUu2VCYRXt8YbMAG5eqNHHaBjlIBEvdrNxEaTb2Vg9oX1zwKpJBQcXeTNxZCaDTfgcw9xg+0e4h+ZVYV0xFUDX5is8a95cA5Kf+OAZDmH+12ZaxGO+2+tA3huD5jo+Gsuu";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);

        VuforiaTrackables beacons = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        beacons.get(0).setName("Wheels");
        beacons.get(1).setName("Tools");
        beacons.get(2).setName("Lego");
        beacons.get(3).setName("Gears");


        waitForStart();

        beacons.activate();
        while(opModeIsActive()){


            //for(int i = 0; i < beacons.size(); i++) {
            Control();
                VuforiaTrackable beac = beacons.get(2);
            targetVisible = ((VuforiaTrackableDefaultListener) beac.getListener()).isVisible();

//            if (targetVisible == false)
//            {
//                lfmotor.setPower(-.125);
//                lbmotor.setPower(-.125);
//                rfmotor.setPower(.125);
//                rbmotor.setPower(.125);
//            }
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beac.getListener()).getPose();

                if (pose != null) {
                    VectorF translation = pose.getTranslation();
                    double zinches = Math.abs(translation.get(2) / 21.6);
                    double yinches = Math.abs(translation.get(1) / 21.6);
                    double xinches = Math.abs(translation.get(0) / 21.6);
//                    if (zinches > 50 || yinches > 50 || xinches > 50) {
//                        zinches = 0;
//                        yinches = 0;
//                        xinches = 0;
//                    }

                    //telemetry.addData(beac.getName() + "Translation", translation);

//                    double degreesToTurn = (Math.toDegrees(Math.atan2(translation.get(0), translation.get(1))));
//                    if (zinches >= 13 && targetVisible == true)
//                    {
//                        lfmotor.setPower(.125);
//                        lbmotor.setPower(.125);
//                        rfmotor.setPower(.125);
//                        rbmotor.setPower(.125);
//                    }
//                    else if (zinches <= 13)
//                    {
//                        lfmotor.setPower(0);
//                        lbmotor.setPower(0);
//                        rfmotor.setPower(0);
//                        rbmotor.setPower(0);
//                    }
//                    if ((degreesToTurn > 40 || degreesToTurn < 25) && (isStoppedAtAngle == false) && targetVisible == true) {
//                        lfmotor.setPower(0);
//                        lbmotor.setPower(0);
//                        rfmotor.setPower(.25);
//                        rbmotor.setPower(.25);
//                        tleft = true;
//
//                    }


//                    else if (degreesToTurn < 30){
//                        rbmotor.setPower(0);
//                        rfmotor.setPower(0);
//                        lfmotor.setPower(.25);
//                        lbmotor.setPower(.25);
//                        tright = true;
//                    }
//                    else if (degreesToTurn <= 40 && degreesToTurn >= 25)
//                    {
//                        lfmotor.setPower(0);
//                        lbmotor.setPower(0);
//                        rfmotor.setPower(0);
//                        rbmotor.setPower(0);
//                        tleft = false;
//                        tright = false;
//                        isStoppedAtAngle = true;
//                    }


//                    while (degreesToTurn < 180)
//                    {
//                        lfmotor.setPower(0);
//                        lbmotor.setPower(0);
//                        rfmotor.setPower(.25);
//                        rbmotor.setPower(.25);
//                    }
//                    while (degreesToTurn > 180)
//                    {
//                        rfmotor.setPower(0);
//                        rbmotor.setPower(0);
//                        lfmotor.setPower(.25);
//                        lbmotor.setPower(.25);
//                    }
//                    if (degreesToTurn == 180 && zinches < 3)
//                    {
//                        rfmotor.setPower(0);
//                        rbmotor.setPower(0);
//                        lfmotor.setPower(0);
//                        lbmotor.setPower(0);
//                    }

//                    telemetry.addData(beac.getName() + "-Degrees", degreesToTurn);
                    telemetry.addData("X", translation.get(0));
                    telemetry.addData("Y", translation.get(1));
                    telemetry.addData("Z", translation.get(2));
                    telemetry.addData("Turning left:", tleft);
                    telemetry.addData("Turning right:", tright);
                    telemetry.addData("Target Visible:", targetVisible);

                    telemetry.addData("Z inches away:", zinches);
                    telemetry.addData("Y inches away:", yinches);
                    telemetry.addData("X inches away:", xinches);

                }

            //}
            telemetry.update();
        }
    }
}

