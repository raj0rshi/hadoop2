package cp_a;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WordCount {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            StringTokenizer strtok = new StringTokenizer(value.toString(), "\n");
            if (strtok.hasMoreTokens()) {
                //strtok.nextToken();
            }
            
            context.write(new Text("value size"), new Text(value.getLength()+""));

            while (strtok.hasMoreTokens()) {
                String Line = strtok.nextToken();

                JSONParser parser = new JSONParser();
                try {

                    JSONObject obj = (JSONObject) parser.parse(Line);

                    JSONObject payload = (JSONObject) obj.get("payload");
                    //System.out.println(payload);

                    JSONArray pages = (JSONArray) payload.get("page");

                    for (int j = 0; j < pages.size(); j++) {
                        JSONObject comment = (JSONObject) pages.get(j);
                        // System.out.println(comment);
                        long id = (long) comment.get("id");
                        // System.out.println(id);
                        Text ID = new Text(Long.toString(id));
                        Text COMMENT = new Text(comment.toString());

                        context.write(ID, COMMENT);

                    }

                } catch (ParseException pe) {

                }

            }

        }
    }

    public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            for (Text val : values) {
                context.write(new Text(), val);
             
                break;
            }
    

        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);

        job.setMapperClass(WordCount.TokenizerMapper.class);
        job.setCombinerClass(WordCount.IntSumReducer.class);
        job.setReducerClass(WordCount.IntSumReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
