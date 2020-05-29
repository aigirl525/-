// ILeoAidl.aidl
package com.xx.leo_service;

// Declare any non-default types here with import statements

import com.xx.leo_service.Person;
import com.xx.leo_service.IOnNewBookArrivedListener;

interface ILeoAidl {
    void addPerson(in Person person);

    List<Person> getPersonList();

    void registerListener(IOnNewBookArrivedListener listener);

    void unregisterListener(IOnNewBookArrivedListener listener);

}
