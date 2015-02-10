package raven.ai.engine;

public enum AttributeType {
	Descriptive, // Can only be one value (e.g. shape, angle)
	Positional, // Can have multiple values, refers to other nodes (e.g. left-of, above)
	Angular,
	Additive, // Can have multiple values (e.g. fill)
	Unknown
}