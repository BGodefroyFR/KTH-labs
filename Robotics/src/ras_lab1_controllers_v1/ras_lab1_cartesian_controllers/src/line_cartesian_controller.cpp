#include "ros/ros.h"
#include "geometry_msgs/Twist.h"

// distances in cm
// angles in radians

int main(int argc, char **argv)
{
  ros::init(argc, argv, "line_cartesian_controller");
  ros::NodeHandle n;

  ros::Publisher pub = n.advertise<geometry_msgs::Twist>("/motor_controller/twist", 1000);

  ros::Rate loop_rate(10);

  while (ros::ok())
  {
    geometry_msgs::Twist msg;

    msg.linear.x = 20.0;
    msg.linear.y = 0.0;
    msg.linear.z = 0.0;
    msg.angular.x = 0.0;
    msg.angular.y = 0.0;
    msg.angular.z = 0.0;

    ROS_INFO("sending...");

    pub.publish(msg);
    ros::spinOnce();
    loop_rate.sleep();
  }

  return 0;
}