package com.sparrow.core.utils.file;

import com.sparrow.core.utils.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileToolHelper {

    public static void copy(File src, File to) {
        if (src == null || !src.exists())
            return;
        if (src.isFile()) {
            if (to.exists() && to.isDirectory())
                to = new File(to, src.getName());
            doSimpleCopy(src, to, new byte[1024]);
        } else
            copyFile(src, to, new byte[1024]);
    }

    /**
     * @param file
     * @param toFile 文件拷贝
     */

    private static void doSimpleCopy(File file, File toFile, byte buffer[]) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(toFile);
            while (true) {
                int len = fis.read(buffer);
                if (len == -1)
                    break;
                fos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param filePath 文件夹路径, deleteSubDir = false 删除自身和以下的子文件 deleteSubDir = true
     *                 只删除子文件
     * @throws IOException 删除文件夹下的所有文件
     */
    public static boolean removeFile(String filePath, boolean deleteSubDir)
            throws IOException {
        if (deleteSubDir) {
            File file = new File(filePath);
            if (!file.isDirectory())
                throw new IOException(" - The file path is not a directory !");
            File subfiles[];
            subfiles = file.listFiles();
            for (int i = 0; i < subfiles.length; i++) {
                forceDeleteFile(subfiles[i]);
            }
        } else {
            forceDeleteFile(new File(filePath));
        }
        return true;
    }

    public static boolean removeFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.isDirectory()) {
            File subfiles[];
            subfiles = file.listFiles();
            for (int i = 0; i < subfiles.length; i++) {
                forceDeleteFile(subfiles[i]);
            }
        } else {
            forceDeleteFile(new File(filePath));
        }
        return true;
    }

    public static boolean removeFile(File file) throws IOException {
        if (file == null || !file.exists())
            return false;
        return removeFile(file, false);
    }

    public static boolean removeFile(File fileDir, boolean deleteSubDir)
            throws IOException {
        if (fileDir == null)
            throw new IOException(" - The file path is null!");
        if (deleteSubDir) {
            if (!fileDir.isDirectory())
                throw new IOException(" - The file path is not a directory !");
            File subfiles[];
            subfiles = fileDir.listFiles();
            for (int i = 0; i < subfiles.length; i++) {
                forceDeleteFile(subfiles[i]);
            }
        } else {
            forceDeleteFile(fileDir);
        }
        return true;
    }

    /**
     * <p>
     * Description: delete sub files
     * </p>
     *
     * @param filePath
     * @param filter
     * @return
     * @throws IOException
     * @author Yzc
     */
    public static boolean removeSub(String filePath, FileFilter filter)
            throws IOException {
        return removeSub(new File(filePath), filter);
    }

    /**
     * <p>
     * Description: remove sub files
     * </p>
     *
     * @param file
     * @param filter
     * @return
     * @throws IOException
     * @author Yzc
     */
    public static boolean removeSub(File file, FileFilter filter)
            throws IOException {
        if (!file.exists() || !file.isDirectory())
            throw new IOException(
                    " - The file is not exist or not a directory !");
        File subfiles[] = file.listFiles();
        for (int i = 0; i < subfiles.length; i++) {
            if (filter != null)
                loopFile(subfiles[i], filter);
            else
                forceDeleteFile(subfiles[i]);
        }
        return false;
    }

    public static void removeSub(String filePath, SFileFilter filter)
            throws IOException {
        removeSub(new File(filePath), filter);
    }

    public static void removeSub(File file, SFileFilter filter)
            throws IOException {
        if (!file.exists() || !file.isDirectory())
            throw new IOException(
                    " - The file is not exist or not a directory !");
        if (filter == null)
            forceDeleteFile(file);
        else
            doRemove(file, filter);
    }

    private static void doRemove(File file, SFileFilter filter)
            throws IOException {
        if (!file.exists())
            throw new IOException("指定文件或者目录不存在:" + file.getName());
        if (file.isFile() && filter.testFile(file))
            forceDeleteFile(file);
        else if (file.isDirectory()) {
            if (filter.testDir(file))
                forceDeleteFile(file);
            else {
                File subs[] = file.listFiles();
                for (int i = 0; i < subs.length; i++)
                    doRemove(subs[i], filter);
            }
        }
    }

    public static List<File> findSubFiles(String fileName, SFileFilter filter) {
        return findSubFiles(new File(fileName), filter);
    }

    public static List<File> findSubFiles(File file, SFileFilter filter) {
        if (filter == null)
            return getSubFiles(file);
        List<File> fileList = new ArrayList<File>();
        find(file, fileList, filter);
        return fileList;
    }

    /**
     * @param file
     * @param fileList
     * @param filter   first test file directory is true
     */
    private static void find(File file, List<File> fileList, SFileFilter filter) {
        if (file == null || !file.exists())
            return;
        if (file.isDirectory()) {
            if (filter.testDir(file)) {
                fileAdd(file, fileList, filter);
            } else {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++)
                    find(files[i], fileList, filter);
            }
        }
    }

    /**
     * @param file
     * @param fileList
     * @param filter   second test file is true
     */
    private static void fileAdd(File file, List<File> fileList,
                                SFileFilter filter) {
        if (file == null || !file.exists())
            return;
        if (file.isFile() && filter.testFile(file))
            fileList.add(file);
        else if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++)
                fileAdd(files[i], fileList, filter);
        }
    }

    public static void main(String args[]) {
        String target = "E:\\workspace\\wl-1\\logistics-1.0";
        File file = new File(target);
        try {
            FileFilter svnFileFilter = new FileFilter() {
                public boolean test(File file) {
                    if (file.isDirectory() && file.getName().endsWith("target")) {
                        System.out.println("DEL file: '"
                                + file.getAbsolutePath() + "'");
                        return true;
                    }
                    return false;
                }
            };
            FileToolHelper.removeSub(file, svnFileFilter);
        } catch (IOException e) {
            e.printStackTrace();
        }

   /*     //target = "D:\\workspace\\boco";
        file = new File(target);
        try {
            FileToolHelper.removeSub(file, new SFileFilter() {
                public boolean testDir(File file) {
                    if (file.getName().endsWith(".svn")) {
                        System.out.println("DEL file: '"
                                + file.getAbsolutePath() + "'");
                        return true;
                    }
                    return false;
                }

                public boolean testFile(File file) {
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * @param file
     * @throws IOException 文件夹的递归删除,先删除目录下的文件,再删除目录
     */
    private static void forceDeleteFile(File file) throws IOException {
        if (!file.exists())
            throw new IOException("指定文件或者目录不存在:" + file.getName());
        if (file.delete())
            return; // 尝试直接删除
        // 如果是文件夹,删除其中所有子项目
        if (file.isDirectory()) {
            File subs[] = file.listFiles();
            for (int i = 0; i < subs.length; i++)
                forceDeleteFile(subs[i]);
        }
        // 先删除子文件内容，最后删除目录
        file.delete();
    }

    /**
     * @param file
     * @throws IOException 文件夹的递归删除,先删除目录下的文件,再删除目录
     */
    private static void loopFile(File file, FileFilter filter)
            throws IOException {
        if (!file.exists())
            throw new IOException("指定文件或者目录不存在:" + file.getName());
        if (filter != null && filter.test(file)) {
            forceDeleteFile(file);
            return;
        } else if (file.isDirectory()) {
            File subs[] = file.listFiles();
            for (int i = 0; i < subs.length; i++) {
                loopFile(subs[i], filter);
            }
        }
    }

    public static boolean isRelative(String file) {
        // unix
        if (file.startsWith("/")) {
            return false;
        }
        // windows
        if ((file.length() > 2) && (file.charAt(1) == ':')) {
            return false;
        }
        return true;
    }

    public static boolean isAbsolute(String file) {
        return !isRelative(file);
    }

    public static File createFile(String file) {
        String path = FileToolHelper.getFileDir(file);
        File f = null;
        if (StringUtils.isEmpty(file))
            return null;
        if (path != null) {
            f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        f = new File(file);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static File createFile(File file) {
        if (file == null)
            return null;
        if (file.exists())
            return file;
        File dir = file;
        if (dir.isFile()) {
            dir = dir.getParentFile();
        }
        if (!dir.mkdirs())
            return null;
        if (file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String getFileDir(String file) {
        if (file == null)
            return null;
        int x = file.lastIndexOf("/");
        if (x >= 0) {
            file = file.substring(0, x);
            return file;
        }
        x = file.lastIndexOf("\\");

        if (x >= 0) {
            file = file.substring(0, x);
            return file;
        } else
            return null;
    }

    public static List<File> getSubFiles(String file) {
        return getSubFiles(new File(file));
    }

    public static List<File> getSubFiles(File file) {
        List<File> fileList = new ArrayList<File>();
        findFile(file, fileList);
        return fileList;
    }

    public static List<File> getSubFiles(String file, String type) {
        return getSubFiles(new File(file), type);
    }

    public static List<File> getSubFiles(File file, String type) {
        List<File> fileList = new ArrayList<File>();
        FilenameFilter filter = new FileNameSelector(type);
        findFile(file, fileList, filter);
        return fileList;
    }

    public static List<File> getSubFiles(File file, String type, String exclude) {
        List<File> fileList = new ArrayList<File>();
        FilenameFilter filter = new FileNameSelector(type, exclude);
        findFile(file, fileList, filter);
        return fileList;
    }

    public static void findFile(File file, List<File> fileList) {
        if (file == null || !file.exists())
            return;
        if (file.isFile()) {
            fileList.add(file);
        } else {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++)
                findFile(files[i], fileList);
        }
    }

    public static void findFile(File file, List<File> fileList,
                                FilenameFilter filter) {
        if (file == null || !file.exists())
            return;
        if (file.isFile()) {
            fileList.add(file);
        } else {
            File files[] = file.listFiles(filter);
            for (int i = 0; i < files.length; i++)
                findFile(files[i], fileList, filter);
        }
    }

    public static void copyFile(String src, String toDir) {
        copyFile(src, toDir, new byte[1024]);
    }

    public static void copyFile(File src, File toDir) {
        copyFile(src, toDir, new byte[1024]);
    }

    public static void copyFile(String src, String toDir, byte buffer[]) {
        File destfile = new File(toDir);
        if (!destfile.exists())
            destfile.mkdirs();
        copyFile(new File(src), new File(toDir), buffer);
    }

    public static void copyFile(File src, File toDir, byte buffer[]) {
        if (src == null || !src.exists())
            return;
        File copyto = new File(toDir, src.getName());
        doCopy(src, copyto, buffer);
        if (src.isDirectory()) {
            File files[] = src.listFiles();
            for (int i = 0; i < files.length; i++)
                copyFile(files[i], copyto, buffer);
        }
    }

    public static void copySubFile(File src, File toDir, byte buffer[]) {
        if (src == null || !src.exists())
            return;
        if (src.isDirectory()) {
            File s[] = src.listFiles();
            for (File f : s)
                copyFile(f, toDir, buffer);
        } else {
            copyFile(src, toDir, buffer);
        }
    }

    private static void doCopySubFile(File src, File toDir, byte buffer[]) {
        if (src == null || !src.exists())
            return;
        File copyto = new File(toDir, src.getName());
        doCopy(src, copyto, buffer);
        if (src.isDirectory()) {
            File files[] = src.listFiles();
            for (int i = 0; i < files.length; i++)
                doCopySubFile(files[i], copyto, buffer);
        }
    }

    public static void copyFile(String src, String toDir, SFileFilter filter,
                                byte buffer[]) {
        copyFile(new File(src), new File(toDir), filter, buffer);
    }

    public static void copyFile(File src, File toDir, SFileFilter filter,
                                byte buffer[]) {
        if (filter == null)
            copyFile(src, toDir, buffer);
        if (src == null || !src.exists())
            return;

        File copyto = new File(toDir, src.getName());

        if (src.isDirectory()) {
            if (filter.testDir(src)) {
                doCopySubFile(src, toDir, buffer);
                return;
            } else {
                File files[] = src.listFiles();
                for (int i = 0; i < files.length; i++)
                    copyFile(files[i], copyto, filter, buffer);
            }
        } else if ((src.isFile() && filter.testFile(src))) {
            doCopy(src, copyto, buffer);
        }
    }

    public static void copyFileEl(String src, String toDir, SFileFilter filter,
                                  byte buffer[]) {
        copyFileEl(new File(src), new File(toDir), filter, buffer);
    }

    public static void copyFileEl(File src, File toDir, SFileFilter filter,
                                  byte buffer[]) {
        if (filter == null)
            copyFile(src, toDir, buffer);
        if (src == null || !src.exists())
            return;
        checkDirectory(src, toDir, filter, buffer);
    }

    public static void copySubFileEl(File src, File toDir, SFileFilter filter,
                                     byte buffer[]) {
        if (src.isDirectory()) {
            File s[] = src.listFiles();
            for (File f : s) {
                if (f.isDirectory())
                    copyFileEl(f, toDir, filter, buffer);
                else
                    copyFile(f, toDir, buffer);
            }
        } else {
            copyFile(src, toDir, buffer);
        }
    }

    private static void checkDirectory(File src, File toDir,
                                       SFileFilter filter, byte[] buffer) {
        if (src.isFile())
            return;
        if (filter.testDir(src)) {
            checkCopyFile(src, toDir, filter, buffer);
            return;
        } else {
            File copyto = new File(toDir, src.getName());
            File files[] = src.listFiles();
            for (int i = 0; i < files.length; i++)
                checkDirectory(files[i], copyto, filter, buffer);
        }
    }

    private static void checkCopyFile(File src, File toDir, SFileFilter filter,
                                      byte[] buffer) {
        File copyto = new File(toDir, src.getName());
        if (src.isDirectory()) {
            doMakeDir(copyto);
            File files[] = src.listFiles();
            for (int i = 0; i < files.length; i++)
                checkCopyFile(files[i], copyto, filter, buffer);
        } else if ((src.isFile() && filter.testFile(src)))
            doCopy(src, copyto, buffer);
    }

    private static void doMakeDir(File dir) {
        dir.mkdirs();
    }

    /**
     * @param file
     * @param toFile 文件拷贝
     */

    private static void doCopy(File file, File toFile, byte buffer[]) {
        if (file == null || !file.exists())
            return;
        if (file.isDirectory()) {
            toFile.mkdir();
            return;
        } else {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            // byte bytes[] = new byte[8192];

            try {
                fis = new FileInputStream(file);
                fos = new FileOutputStream(toFile);
                while (true) {
                    int len = fis.read(buffer);
                    if (len == -1)
                        break;
                    fos.write(buffer, 0, len);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class FileNameSelector implements FilenameFilter {
        private String fileType;
        private String exclude;

        FileNameSelector(String type) {
            fileType = ((type == null) ? "" : type.toLowerCase());
        }

        FileNameSelector(String type, String exclude) {
            this.fileType = ((type == null) ? "" : type.toLowerCase());
            this.exclude = ((exclude == null) ? "" : exclude.toLowerCase());
        }

        public boolean accept(File dir, String name) {
            if (exclude != null)
                return name.toLowerCase().endsWith(fileType)
                        && !name.toLowerCase().startsWith(exclude);
            else
                return name.toLowerCase().endsWith(fileType);
        }
    }

    public static int getFileCounts(File file) {
        if (file == null || !file.exists())
            return 0;
        if (file.isFile())
            return 1;
        else if (file.isDirectory()) {
            File[] files = file.listFiles();
            int m = 0;
            for (File fil : files) {
                if (fil.isDirectory())
                    m += getFileCounts(fil);
                else if (file.isFile())
                    m += 1;
            }
            return m;
        } else
            return 0;
    }
}
