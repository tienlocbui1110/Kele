package com.lh.component.common;

import com.lh.IPackage.IResourceManager;
import com.lh.component.exception.ResourceIOException;
import com.lh.component.exception.ResourceNotFoundException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

public class ResourceReader implements IResourceManager {

    private File resource;

    public ResourceReader(String resourceName) {
        URL url = getClass().getClassLoader().getResource(resourceName);
        if (url == null) {
            throw new ResourceNotFoundException("Resource " + resourceName + " does not exist");
        }

        resource = new File(url.getFile());
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(resource);
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException(e);
        }
    }

    @Override
    public String readContent() {
        try {
            return new String(Files.readAllBytes(resource.toPath()));
        } catch (IOException e) {
            throw new ResourceIOException(e);
        }
    }

    @Override
    public String[] readLines() {
        try {
            return Files.readAllLines(resource.toPath()).toArray(new String[0]);
        } catch (IOException e) {
            throw new ResourceIOException(e);
        }
    }
}
