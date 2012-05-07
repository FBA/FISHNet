/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchjsf.images;

import searchjsf.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author mhaft
 */
public class ImageIds {

    private long mainImgId = 0;
    private long thumbnailId = 0;
    private long mediumSizeImgId = 0;
    private String imgTitle = "";

    private ApplicationBean1 appBean;

    public ImageIds(long mainImgId) {
        this.mainImgId = mainImgId;
    }

    public String getImgTitle() {
        return imgTitle;
    }

    public void setImgTitle(String imgTitle) {
        this.imgTitle = imgTitle;
    }

    public long getMainImgId() {
        return this.mainImgId;
    }

    public long getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(long thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public void setMediumSizeImgId(long mediumSizeImgId) {
        this.mediumSizeImgId = mediumSizeImgId;
    }

    public long getMediumSizeId() {
        return mediumSizeImgId;
    }

    private ImageIds getThumbnailAndMediumSizeIds(long gcl_derivativeSourceId) {
        Connection con = getSQLConnection();
        ImageIds imgIds = new ImageIds(gcl_derivativeSourceId);
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT a.gcl_id FROM gtb_derivative a, gtb_childentity b " +
                    "WHERE a.gcl_id = b.gcl_id AND b.gcl_parentid = " + gcl_derivativeSourceId + " ORDER BY a.gcl_derivativeType ");
            if (rs.next()) {
                imgIds.setThumbnailId(rs.getLong(1));
            }
            rs = stmt.executeQuery("SELECT gcl_title FROM gtb_item WHERE gcl_id = " + gcl_derivativeSourceId);
            if (rs.next()) {
                imgIds.setImgTitle(rs.getString(1));
            }
//            System.out.println("getThumbnailId: " + imgIds.getThumbnailId());
//            System.out.println("getImgTitle: " + imgIds.getImgTitle());
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return imgIds;

    }

    private Connection getSQLConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://MYSQLSERVER/gallery";
            try {
                con = DriverManager.getConnection(url, "USERNAME", "PASSWORD");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return con;
    }
}