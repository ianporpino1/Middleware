package invoker.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamResolver {
    public static Map<String, String> extractQueryParams(String url) {
        Map<String, String> queryParams = new HashMap<>();

        // Verifica se a URL contem query params
        if (url.contains("?")) {
            String queryString = url.split("\\?")[1];
            String[] pairs = queryString.split("&");

            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return queryParams;
    }


    public static Map<String, String> extractPathVariables(String routeTemplate, String url) {
        Map<String, String> pathVariables = new HashMap<>();

        String path = url.split("\\?")[0];

        String regex = templateToRegex(routeTemplate);

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(path);

        if (matcher.matches()) {
            for (String groupName : getTemplateVariableNames(routeTemplate)) {
                pathVariables.put(groupName, matcher.group(groupName));
            }
        }
        return pathVariables;
    }

    private static String templateToRegex(String template) {
        return "^" + template
                .replaceAll("\\{([a-zA-Z0-9_]+)\\}", "(?<$1>[a-zA-Z0-9]+)")
                .replace("/", "\\/") + "$";
    }

    private static List<String> getTemplateVariableNames(String template) {
        List<String> variableNames = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}").matcher(template);
        while (matcher.find()) {
            variableNames.add(matcher.group(1));
        }
        return variableNames;
    }
    
}
