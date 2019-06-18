package com.lh.IPackage;

import java.io.InputStream;

public interface IResourceManager {
    InputStream getInputStream();

    String readContent();

    String[] readLines();
}
