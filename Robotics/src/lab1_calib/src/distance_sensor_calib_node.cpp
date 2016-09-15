#include "ros/ros.h"
#include "std_msgs/Float64.h"

int main(int argc, char **argv)
{
  ros::init(argc, argv, "distance_sensor_calib_node");

  ros::NodeHandle n;

  ros::Publisher pub = n.advertise<std_msgs::Float64>("/distance_sensor_test_node/distance", 1000);

  ros::Rate loop_rate(10);

  double dist = 0.0;
  while (ros::ok())
  {
    std_msgs::Float64 msg;
    msg.data = dist;

    dist += 0.01;
    if(dist > 0.8)
      dist = 0.0;

    ROS_INFO("%f", msg.data);

    pub.publish(msg);
    ros::spinOnce();
    loop_rate.sleep();
  }

  return 0;
}