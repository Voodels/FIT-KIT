import type { NextConfig } from "next";
import path from "path";

const nextConfig: NextConfig = {
  transpilePackages: [
    "react-native-body-highlighter",
    "react-native-svg",
    "react-native",
    "react-native-web",
  ],
  turbopack: {},
  webpack: (config) => {
    config.resolve.alias = {
      ...(config.resolve.alias || {}),
      // 1. Redirect the main library to web
      "react-native$": "react-native-web",
      "react-native": path.resolve(__dirname, "shims/react-native.js"),
      "react-native/index.js": path.resolve(__dirname, "shims/react-native.js"),
      
      // 2. THE PERMANENT FIX: Map mobile SVG components to standard Web SVG elements
      "react-native-svg": path.resolve(__dirname, "shims/react-native-svg.js"),

      // 3. Catch any deep native imports and null them out
      "react-native/Libraries/Utilities/codegenNativeComponent": path.resolve(
        __dirname,
        "shims/codegenNativeComponent.js"
      ),
    };
    
    return config;
  },
};

export default nextConfig;
