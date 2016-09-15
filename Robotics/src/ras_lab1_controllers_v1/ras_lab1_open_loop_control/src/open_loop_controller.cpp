#include "ros/ros.h"
#include "std_msgs/Float64.h"
#include <ras_lab1_msgs/PWM.h> 

int main(int argc, char **argv)
{
  ros::init(argc, argv, "open_loop_controller");

  ros::NodeHandle n;

  ros::Publisher pub = n.advertise<ras_lab1_msgs::PWM>("/kobuki/pwm", 1000);

  ros::Rate loop_rate(10);

  while (ros::ok())
  {
    ras_lab1_msgs::PWM msg;
    msg.PWM1 = 255;
    msg.PWM2 = 255;

    ROS_INFO("sending...");

    pub.publish(msg);
    ros::spinOnce();
    loop_rate.sleep();
  }

  return 0;
}