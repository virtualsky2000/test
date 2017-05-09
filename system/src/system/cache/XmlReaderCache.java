package system.cache;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import system.utils.FileUtils;
import system.xml.XmlNode;
import system.xml.XmlReader;

public class XmlReaderCache extends XmlReader {

    private static final Map<String, XmlReaderCache> mapCache = new ConcurrentHashMap<>();

    private long times = 0;

    public static XmlReaderCache load(String fileName) {
        return load(FileUtils.getFile(fileName), Charset.defaultCharset());
    }

    public static XmlReaderCache load(String fileName, Charset charset) {
        return load(FileUtils.getFile(fileName), charset);
    }

    public static XmlReaderCache load(File file, Charset charset) {
        String fileName = file.getAbsolutePath();

        if (!mapCache.containsKey(fileName)) {
            XmlReaderCache cache = new XmlReaderCache(file, charset);
            cache.load();

            mapCache.put(fileName, cache);

            return cache;
        } else {
            return mapCache.get(fileName);
        }
    }

    public XmlReaderCache(File file, Charset charset) {
        super(file, charset);
    }

    synchronized public void load() {
        long lastTimes = file.lastModified();
        if (times < lastTimes) {
            // 前回読込後、ファイルが更新された場合再読込
            super.load();

            times = lastTimes;
        }
    }

    public XmlNode getNode(String path) {
        load();
        return super.getNode(path);
    }

/*    public List<XmlNode> getSubNodes(XmlNode parent, String nodeName) {
        load();
        return super.getSubNodes(parent, nodeName);
    }

    public List<XmlNode> getNodes(String nodeName) {
        load();
        return super.getNodes(nodeName);
    }*/

}
