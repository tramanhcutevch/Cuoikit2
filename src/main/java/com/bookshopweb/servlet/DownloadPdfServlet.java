package com.bookshopweb.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/download-pdf")
public class DownloadPdfServlet extends HttpServlet {

    // Thư mục gốc lưu file PDF trên server, bạn đổi đường dẫn này cho đúng
    private static final String PDF_ROOT = "/absolute/path/to/pdf/folder";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filePath = request.getParameter("filePath");

        if (filePath == null || filePath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Missing filePath parameter\"}");
            return;
        }

        // Tránh lỗi path traversal (đường dẫn lạc hướng)
        if (filePath.contains("..")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid filePath\"}");
            return;
        }

        Path pdfFile = Paths.get(PDF_ROOT, filePath);
        if (!Files.exists(pdfFile)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"File not found\"}");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + pdfFile.getFileName().toString() + "\"");

        try (InputStream in = Files.newInputStream(pdfFile);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
}
