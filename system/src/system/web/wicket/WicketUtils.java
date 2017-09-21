package system.web.wicket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class WicketUtils {

    public static Component findComponent(Iterator<Component> iterator, String id) {
        while (iterator.hasNext()) {
            Component component = iterator.next();

            if (component.getId().equals(id)) {
                return component;
            }
            if (component instanceof WebMarkupContainer) {
                component = findComponent((WebMarkupContainer) component, id);
                if (component != null) {
                    return component;
                }
            }

        }

        return null;
    }

    public static Component findComponent(WebMarkupContainer parent, String id) {
        return findComponent(parent.iterator(), id);
    }

    public static <T> List<T> getChilds(WebMarkupContainer parent, Class<T> clazz) {
        List<T> lstComponent = new ArrayList<T>();
        Iterator<Component> iterator = parent.iterator();

        while (iterator.hasNext()) {
            Component component = iterator.next();
            if (isExtend(clazz, component.getClass())) {
                lstComponent.add(clazz.cast(component));
            } else if (component instanceof WebMarkupContainer) {
                List<T> subComponents = getChilds((WebMarkupContainer) component, clazz);
                if (!subComponents.isEmpty()) {
                    lstComponent.addAll(subComponents);
                }
            }
        }

        return lstComponent;
    }

    private static boolean isExtend(Class<?> A, Class<?> B) {
        do {
            if (B.equals(A)) {
                return true;
            }
            B = B.getSuperclass();
        } while (!B.equals(Object.class));

        return false;
    }

}
