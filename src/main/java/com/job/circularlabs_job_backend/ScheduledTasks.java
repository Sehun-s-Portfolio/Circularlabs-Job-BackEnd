package com.job.circularlabs_job_backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by rajeevkumarsingh on 02/08/17.
 */
@Component
@Transactional
public class ScheduledTasks {

    @PersistenceContext
    EntityManager em;

    @Value("${lastnum_file_path}")
    String lastnum_file_path;

    private String sql;
    FileWriter fw = null;
    FileReader fr = null;
    BufferedReader br = null;
    String line = "";
    int product_detail_history_id;
    int proc_cnt = 30;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    //@Scheduled(fixedRate = 2000)
    @Scheduled(fixedRate = 10 * 1000)
    public void scheduleTaskWithFixedRate() {
        //logger.info("Fixed Rate Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()) );
        //String sql = "update rental set rental_stat = 'C'";
        //int result = em.createNativeQuery(sql).executeUpdate();
        //System.out.println(result);

        try{
            fr = new FileReader(lastnum_file_path);
            br = new BufferedReader(fr);
            line = br.readLine();
            br.close();
            fr.close();
            System.out.println(line);
            

            //sql = "select product_detail_history_id, product_serial_code, status from product_detail_history where product_detail_history_id > " + line + " and product_detail_history_id < " + Integer.toString(Integer.parseInt(line) + proc_cnt) + " and status = '회수' order by product_detail_history_id asc";
            sql = "select product_detail_history_id, product_serial_code, status, client_code from product_detail_history where product_detail_history_id > " + line + " and product_detail_history_id < " + Integer.toString(Integer.parseInt(line) + proc_cnt) + " order by product_detail_history_id asc";
            System.out.println(sql);
            //int cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
            //System.out.println(cnt);
            List<Object[]> list = em.createNativeQuery(sql).getResultList();
            //System.out.println("-----" + "," + list.size());

            if(list.size() > 0){
                product_detail_history_id = 0;

                Date d = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = format.format(d);
                for(int i = 0; i < list.size(); i++){
                    Object[] o = list.get(i);
                    System.out.println(o[0] + "," + o[1] + "," + o[2] + "," + o[3]);
                    //System.out.println("------------------------------------------------");


                    if("회수".equals(o[2].toString())){
                        sql = "update rental set rental_stat = 'C', modified_at = '" + currentDateTime + "' where rental_stat = 'R' and prod_serial = '" + o[1] + "' and 'Y' = (select cust_type from member where classification_code = '" + o[3] + "')";
                        //System.out.println(sql);
                        int result = em.createNativeQuery(sql).executeUpdate();
                        System.out.println(result + "," + sql);

                        product_detail_history_id = Integer.parseInt(o[0].toString());
                        //System.out.println("----------" + ":" + Integer.toString(product_detail_history_id));
                    }else{ //입고
                        product_detail_history_id = Integer.parseInt(o[0].toString());
                    }
                }

                //int cnt = Integer.parseInt(line) + 4;
                //System.out.println("-----------------------");
                System.out.println(product_detail_history_id);
                fw = new FileWriter(lastnum_file_path);
                fw.write(Integer.toString(product_detail_history_id));
                fw.close();

            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
