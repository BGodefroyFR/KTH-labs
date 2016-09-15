#include "ros/ros.h"
#include "geometry_msgs/Twist.h"
#include "ras_lab1_msgs/Encoders.h"
#include "ras_lab1_msgs/PWM.h" 

// distances in cm
// angles in radians

const double PI = 3.14159;

const double wheelbase = 23.0;
const double radius = 3.5;
const int tick_per_row = 360;
const int control_frequency = 10;

const double alpha1 = 0.16;
const double alpha2 = 0.01;

double linear_x, angular_z;
int delta_encoder1, delta_encoder2;
double desired1, desired2;

ros::Publisher pub;

void computePWN()
{
  double pw1 = (linear_x - (wheelbase/2.0) * angular_z) / radius * 15.0;
  double pw2 = (linear_x + (wheelbase/2.0) * angular_z) / radius * 15.0;

  double initial_pw1 = pw1, initial_pw2 = pw2;

  double estimated_w1 = (double)((((double)delta_encoder1) * 2.0 * PI * ((double)control_frequency)) / ((double)tick_per_row));
  double estimated_w2 = (double)((((double)delta_encoder2) * 2.0 * PI * ((double)control_frequency)) / ((double)tick_per_row));

  pw1 += alpha1 * (desired1 - estimated_w1);
  pw2 += alpha2 * (desired2 - estimated_w2);

  ROS_INFO("[%f, %f, %f, %f]", (alpha1 * (desired1 - estimated_w1)), (alpha2 * (desired2 - estimated_w2)), pw1, pw2);

  desired1 = initial_pw1;
  desired2 = initial_pw2;

  ras_lab1_msgs::PWM msg;
  msg.PWM1 = pw1;
  msg.PWM2 = pw2;

  ROS_INFO("sending...");
  pub.publish(msg);
}

void cartesianController_callback(const geometry_msgs::Twist::ConstPtr& msg)
{
  linear_x = (double)msg->linear.x;
  angular_z = (double)msg->angular.z;
  computePWN();
}

void encodersController_callback(const ras_lab1_msgs::Encoders::ConstPtr& msg)
{
  delta_encoder1 = msg->delta_encoder1;
  delta_encoder2 = msg->delta_encoder2;
}

int main(int argc, char **argv)
{
  ros::init(argc, argv, "motor_controller");
  ros::NodeHandle n;

  desired1 = 0.0;
  desired2 = 0.0;

  pub = n.advertise<ras_lab1_msgs::PWM>("/kobuki/pwm", 1000);

  ros::Subscriber sub_cartesianController = n.subscribe("/motor_controller/twist", 1000, cartesianController_callback);
  ros::Subscriber sub_encodersController = n.subscribe("/kobuki/encoders", 1000, encodersController_callback);

  ros::spin();

  return 0;
}