#include "ros/ros.h"
#include "geometry_msgs/Twist.h"
#include "ras_lab1_msgs/ADConverter.h"

// distances in cm
// angles in radians

double ch1;
double ch2;

const double alpha = -0.01;

void adc_callback(const ras_lab1_msgs::ADConverter::ConstPtr& msg)
{
  ch1 = (double)msg->ch1;
  ch2 = (double)msg->ch2;
}

int main(int argc, char **argv)
{
  ros::init(argc, argv, "circle_cartesian_controller");
  ros::NodeHandle n;

  ch1 = 0.0;
  ch2 = 0.0;

  ros::Publisher pub = n.advertise<geometry_msgs::Twist>("/motor_controller/twist", 1000);
  ros::Subscriber sub_adc = n.subscribe("/kobuki/adc", 1000, adc_callback);

  ros::Rate loop_rate(10);

  while (ros::ok())
  {
    geometry_msgs::Twist msg;

    msg.linear.x = 25.0;
    msg.linear.y = 0.0;
    msg.linear.z = 0.0;
    msg.angular.x = 0.0;
    msg.angular.y = 0.0;
    msg.angular.z = alpha * (ch1 - ch2);

    ROS_INFO("sending...");

    pub.publish(msg);
    ros::spinOnce();
    loop_rate.sleep();
  }

  return 0;
}