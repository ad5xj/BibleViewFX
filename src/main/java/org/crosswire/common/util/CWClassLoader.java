package org.crosswire.common.util;

import java.net.URI;
import java.net.URL;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class CWClassLoader extends ClassLoader 
{
    private static URI[] homes;

    private Class<?> owner;


    @SuppressWarnings("unused")
    CWClassLoader()                       { owner = CallContext.getCallingClass(); }
    CWClassLoader(Class<?> resourceOwner) { owner = resourceOwner; }

    public ClassLoader getClassLoader() 
    {
        ClassLoader loader = pickLoader(Thread.currentThread().getContextClassLoader(), owner.getClassLoader());
        return pickLoader(loader, ClassLoader.getSystemClassLoader());
    }

    @Override
    public URL findResource(String search) 
    {
        URL resource = null;
        if (search == null || search.length() == 0) { return resource; }
        if (search.charAt(0) != '/') { resource = findResource('/' + search); }
        if (resource == null) 
        {
            String newsearch = adjustPackageSearch(search);
            if (!search.equals(newsearch)) { resource = findResource(newsearch); }
        }

        if (resource == null) 
        {
            String newsearch = adjustPathSearch(search);
            if (!search.equals(newsearch)) { resource = findResource(newsearch); }
        }

        if (resource == null) 
        {
            URI homeResource = findHomeResource(search);
            if (homeResource != null) { resource = NetUtil.toURL(homeResource); }
        }

        if (resource == null) { resource = owner.getResource(search); }
        if (resource == null) { resource = getClassLoader().getResource(search); }
        if (resource == null) { resource = ClassLoader.getSystemResource(search); }
        if (resource == null) { resource = super.findResource(search); }
        return resource;
    }

    public static CWClassLoader instance(Class<?> resourceOwner) 
    {
        return AccessController.<CWClassLoader>doPrivileged(new PrivilegedLoader<>(resourceOwner));
    }

    public static CWClassLoader instance() 
    {
        Class<? extends Object> resourceOwner = CallContext.getCallingClass();
        return instance(resourceOwner);
    }

    public static synchronized URI getHome(int i) 
    {
        if (i > 0 && i < homes.length) {
            return homes[i];
        }
        return null;
    }

    public static synchronized void setHome(URI[] newHomes) 
    {
        homes = new URI[newHomes.length];
        System.arraycopy(newHomes, 0, homes, 0, newHomes.length);
    }

    public static URI findHomeResource(String search) 
    {
        if (homes == null) { return null; }
        
        for (int i = 0; i < homes.length; i++) 
        {
            URI homeURI = homes[i];
            URI newuri = NetUtil.lengthenURI(homeURI, search);
            if (NetUtil.canRead(newuri)) { return newuri; }
        }
        return null;
    }

    private String adjustPackageSearch(String aSearch) 
    {
        String search = aSearch;
        if ( search.indexOf('/', 1) == -1 ) // Does not end with URL separator (/)
        {
            String className = owner.getName();
            String pkgPrefix = className.substring(0, className.lastIndexOf('.') + 1);
            if (search.charAt(0) == '/') 
            {
                String part = search.substring(1);
                if (!part.startsWith(pkgPrefix)) { search = '/' + pkgPrefix + part; }
            } 
            else if (!search.startsWith(pkgPrefix)) 
            {
                search = pkgPrefix + search;
            }
        }
        return search;
    }

    private String adjustPathSearch(String aSearch) 
    {
        String search = aSearch;
        if ( search.indexOf('/', 1) != -1 ) 
        {
            if (search.charAt(0) == '/') 
            {
                search = '/' + search.substring(1).replace('/', '.');
            } 
            else 
            {
                search = search.replace('/', '.');
            }
        }
        return search;
    }

    private static ClassLoader pickLoader(ClassLoader loader1, ClassLoader loader2) 
    {
        ClassLoader loader = loader2;
        if (loader1 != loader2) 
        {
            loader = loader1;
            if (loader1 == null) 
            {
                loader = loader2;
            } 
            else 
            {
                for (ClassLoader curloader = loader2; curloader != null; curloader = curloader.getParent()) {
                    if (curloader == loader1) 
                    {
                        loader = loader2;
                        break;
                    }
                }
            }
        }
        return loader;
    }

    private static class PrivilegedLoader<T> implements PrivilegedAction<T> {

        private Class<?> owningClass;

        PrivilegedLoader(Class<?> resourceOwner) {
            owningClass = resourceOwner;
        }

        @Override
        public T run() 
        {
            return (T) new CWClassLoader(owningClass);
        }
    }
}
