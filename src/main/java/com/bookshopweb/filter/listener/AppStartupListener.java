package com.bookshopweb.filter.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.jdbi.v3.core.Jdbi;


import com.bookshopweb.dao.OrderPdfTaskDAO;
import com.bookshopweb.service.OrderPdfTaskWorker;
import com.bookshopweb.utils.JdbiUtils;

@WebListener
public class AppStartupListener implements ServletContextListener {

    private Thread workerThread;
    private OrderPdfTaskWorker worker;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== Ứng dụng khởi động: Bắt đầu worker tạo PDF ===");

        Jdbi jdbi = JdbiUtils.createInstance();
        OrderPdfTaskDAO taskDAO = new OrderPdfTaskDAO(jdbi);

        worker = new OrderPdfTaskWorker(taskDAO);

        workerThread = new Thread(worker);
        workerThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== Ứng dụng đóng: Dừng worker tạo PDF ===");
        if (worker != null) {
            worker.stop();
        }
        if (workerThread != null) {
            try {
                workerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
