#! /usr/bin/env node
const { spawn, exec } = require('child_process')
const replace = require('replace-in-file')

const spawnCommand = (cmd, args, options, onOutput) => {
  return new Promise((resolve, reject) => {
    const child = spawn(cmd, args, options)

    child.stdout?.on('data', (data) => {
      onOutput && onOutput(data)
    })

    child.on('close', function (code) {
      // Should probably be 'exit', not 'close'
      // *** Process completed
      resolve(code)
    })
    child.on('error', function (err) {
      // *** Process creation failed
      reject(err)
    })
  })
}

const replaceName = async (oldname, newName) => {
  const results = (
    await replace({
      // files: '**',
      files: [
        'package-lock.json',
        'package.json',
        'README.md',
        'example/tsconfig.json',
        'example/index.js',
        'bin/index.js',
      ],
      from: new RegExp(oldname, 'g'),
      to: newName,
      // ignore: [
      //   './scripts/deploy.js',
      //   'node_modules/**/*',
      //   './example/node_modules/**/*',
      // ],
    })
  )
    .filter((result) => result.hasChanged)
    .map((result) => result.file)
}

const main = async () => {
  const npmServer = process.argv
    .find((x) => x.startsWith('npm'))
    .replace('npm=', '')

  await spawnCommand('yarn', ['compile'], {
    cwd: process.cwd(),
    env: process.env,
    stdio: [process.stdin, process.stdout, process.stderr],
    encoding: 'utf-8',
  })

  if (npmServer === 'digikala') {
    await replaceName('digikala/digi-push', 'ravenclaw/app-update')
    await replaceName('digi-push', 'app-update')
    await replaceName('https://npm.pkg.github.com', 'http://localhost:4873')
    // await spawnCommand(
    //   'npm',
    //   ['publish', '--registry', 'http://localhost:4873'],
    //   {
    //     cwd: process.cwd(),
    //     env: process.env,
    //     stdio: [process.stdin, process.stdout, process.stderr],
    //     encoding: 'utf-8',
    //   }
    // )
  } else {
    await replaceName('ravenclaw/app-update', 'digikala/digi-push')
    await replaceName('app-update', 'digi-push')
    await replaceName('http://localhost:4873', 'https://npm.pkg.github.com')

    // await spawnCommand(
    //   'npm',
    //   ['publish', '--registry', 'https://npm.pkg.github.com'],
    //   {
    //     cwd: process.cwd(),
    //     env: process.env,
    //     stdio: [process.stdin, process.stdout, process.stderr],
    //     encoding: 'utf-8',
    //   }
    // )
  }
}

main()
