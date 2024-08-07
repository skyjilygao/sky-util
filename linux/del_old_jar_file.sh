#!/bin/bash
d=$1
jar_parent_dir=/home1/java
lastn=3
echo clear dir: $d
if [[ -z $d ]];then
  echo "please type dir name"  
  exit 1
fi
jarpath=$jar_parent_dir/$d
cd $jarpath
jarlist=`sudo ls -ltr $jarpath/*/*.jar |awk -F" " '{print $9}' |awk -F"/" '{print $NF}'|sort -u`
for jarname in $jarlist
do
# echo "====>>>>>> check jar name:$jarname <<<<<<====== $jarpath/*/$jarname "
# sudo ls -ltr $jarpath/$jarname
deljar=`sudo ls -ltr $jarpath/*/$jarname |awk -F" " '{print $9}' |head -n -$lastn`

# echo $deljar
for dj in $deljar
do
echo "delete jar file: $dj"
sudo rm -rf $dj
done
done

# 目标目录
target_dir=$jarpath
#echo "dir: "$target_dir
# 检查目标目录是否存在
if [ ! -d "$target_dir" ]; then
  echo "目录 $target_dir 不存在"
  exit 1
fi

# 遍历以 java# 开头的子目录
for dir in "$target_dir"/java_#*; do
 # echo "java# dir: " $dir
  # 检查是否为目录
  if [ -d "$dir" ]; then
    # 检查目录下是否存在 .jar 文件
    jar_files=$(find "$dir" -maxdepth 1 -name '*.jar')
    if [ -z "$jar_files" ]; then
      # 如果不存在 .jar 文件，则删除该目录
      echo "删除目录: $dir"
      rm -rf "$dir"
    else
      echo "目录 $dir 包含 .jar 文件，跳过删除"
    fi
  fi
done
