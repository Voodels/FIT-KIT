const React = require("react");

// Helper to map React Native props to Web props
const mapProps = (props) => {
  const newProps = { ...props };
  
  // Map onPress to onClick for web compatibility
  if (newProps.onPress) {
    newProps.onClick = newProps.onPress;
    delete newProps.onPress;
  }

  // Remove mobile-only props that cause React DOM warnings
  delete newProps.accessible;
  delete newProps.accessibilityLabel;
  delete newProps.accessibilityRole;
  delete newProps.accessibilityStates;
  delete newProps.accessibilityHint;

  // Ensure fill and stroke are handled correctly if they are missing
  if (newProps.fill === undefined) newProps.fill = "currentColor";
  
  // Standardize pointer cursor for interactive elements
  if (newProps.onClick) {
    newProps.style = { 
      cursor: 'pointer', 
      transition: 'fill 0.2s ease',
      ...(newProps.style || {}) 
    };
  }

  return newProps;
};

// Standard Web SVG components
const Svg = ({ children, ...props }) => (
  React.createElement("svg", mapProps(props), children)
);

const Path = (props) => React.createElement("path", mapProps(props));
const G = ({ children, ...props }) => React.createElement("g", mapProps(props), children);
const Rect = (props) => React.createElement("rect", mapProps(props));
const Circle = (props) => React.createElement("circle", mapProps(props));
const Polygon = (props) => React.createElement("polygon", mapProps(props));
const Polyline = (props) => React.createElement("polyline", mapProps(props));
const Line = (props) => React.createElement("line", mapProps(props));
const Ellipse = (props) => React.createElement("ellipse", mapProps(props));
const Text = ({ children, ...props }) => React.createElement("text", mapProps(props), children);
const TSpan = ({ children, ...props }) => React.createElement("tspan", mapProps(props), children);
const Defs = ({ children, ...props }) => React.createElement("defs", mapProps(props), children);
const Stop = (props) => React.createElement("stop", mapProps(props));
const LinearGradient = ({ children, ...props }) => React.createElement("linearGradient", mapProps(props), children);
const RadialGradient = ({ children, ...props }) => React.createElement("radialGradient", mapProps(props), children);

// Attach components to Svg
Svg.Path = Path;
Svg.G = G;
Svg.Rect = Rect;
Svg.Circle = Circle;
Svg.Polygon = Polygon;
Svg.Polyline = Polyline;
Svg.Line = Line;
Svg.Ellipse = Ellipse;
Svg.Text = Text;
Svg.TSpan = TSpan;
Svg.Defs = Defs;
Svg.Stop = Stop;
Svg.LinearGradient = LinearGradient;
Svg.RadialGradient = RadialGradient;

// Export everything
module.exports = Svg;
module.exports.default = Svg;
module.exports.Svg = Svg;
module.exports.Path = Path;
module.exports.G = G;
module.exports.Rect = Rect;
module.exports.Circle = Circle;
module.exports.Polygon = Polygon;
module.exports.Polyline = Polyline;
module.exports.Line = Line;
module.exports.Ellipse = Ellipse;
module.exports.Text = Text;
module.exports.TSpan = TSpan;
module.exports.Defs = Defs;
module.exports.Stop = Stop;
module.exports.LinearGradient = LinearGradient;
module.exports.RadialGradient = RadialGradient;
module.exports.__esModule = true;
