const fs = require('fs');
const path = require('path');
const YAML = require('yaml');

const LANG_FOLDER = 'src/main/resources/lang/';
const langFiles = fs.readdirSync(LANG_FOLDER);

let error = false;

const fileKeys = new Map();

function getAllKeys(baseKey, obj) {
	const keys = [];

	for (const key in obj) {
		if (typeof obj[key] == 'object' && !Array.isArray(obj[key])) {
			keys.push(...getAllKeys(baseKey + key + '.', obj[key]));
		} else {
			keys.push(baseKey + key);
		}
	}

	return keys;
}

for (const file of langFiles) {
	const content = fs.readFileSync(path.join(LANG_FOLDER, file), 'utf-8');
	const yaml = YAML.parse(content);

	fileKeys.set(file, getAllKeys('', yaml));
}

const fileKeysArr = Array.from(fileKeys.values());

// Time complexity go nyoooom
// O(n² - n)
fileKeys.forEach((keys, file) => {
	console.log(`Comparing "${file}"...`);

	fileKeys.forEach((keysToCompare, fileToCompare) => {
		if (file == fileToCompare) return;

		for (const key of keys) {
			if (!keysToCompare.includes(key)) {
				error = true;

				console.error(
					`Error comparing "${file}": Key "${key}" does not exist in "${fileToCompare}"`
				);
			}
		}
	});
});

if (error) {
	console.log('Lang file check failed');
	process.exit(1);
} else {
	console.log('Lang file check successful');
}
