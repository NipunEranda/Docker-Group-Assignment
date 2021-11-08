package org.sliit.web.api;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class ApplicationLifeCycle {

	void onStart(@Observes StartupEvent ev) {
		System.out.println("\nSystem Up and running\n");
	}

	void onStop(@Observes ShutdownEvent ev) {
		System.out.println("\nSystem Shutdown!\n");
	}

}
