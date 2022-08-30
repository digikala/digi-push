#! /usr/bin/env node

const cli = require('commander')
const chalk = require('chalk')
const { exec } = require('child_process')

const logger = {
  echo(message, level = 0, wrapper = null, asString = false) {
    let output
    if (typeof message === 'string') {
      const string = '  '.repeat(level) + message
      output = wrapper ? wrapper(string) : string
    } else {
      output = message
    }

    if (asString) {
      return output
    }

    console.log(output)
  },

  error(message, level = 0, asString = false) {
    return this.echo(message, level, chalk.red, asString)
  },

  success(message, level = 0, asString = false) {
    return this.echo(message, level, chalk.green, asString)
  },

  warning(message, level = 0, asString = false) {
    return this.echo(message, level, chalk.yellow, asString)
  },

  notice(message, level = 0, asString = false) {
    return this.echo(message, level, chalk.blue, asString)
  },

  info(message, level = 0, asString = false) {
    return this.echo(message, level, null, asString)
  },

  line(asString = false) {
    return this.echo('', 0, null, asString)
  },
}

const command = (cmd) => {
  return new Promise((res, rej) => {
    const child = exec(cmd, (err, stdout, stderr) => {
      if (err) {
        logger.error(`error: ${err}`)
        rej(err)
        return
      }

      res(stdout)
    })

    child.stdout.on('data', function (data) {
      console.log(data.toString())
    })
  })
}

const bundle = async (options) => {
  const { outputPath, platform } = options

  const dir = `${outputPath}/dg-bundle`.replace('//', '/')

  if (platform === 'android' || platform === 'both') {
    logger.line()
    logger.notice('Android Bundling')
    await command(
      `rm -rf ${dir} && mkdir ${dir} && npx react-native bundle --platform android --dev false --entry-file index.js --bundle-output ${dir}/index.android.bundle --assets-dest ${dir} && cd ${dir} && zip -r ../android-bundle.zip * && cd ../ &&  rm -r dg-bundle`
    )
    logger.line()
    logger.success(`Android Bundling Done!`)
    logger.success(`Path: ${outputPath}/android-bundle.zip`.replace('//', '/'))
  }

  if (platform === 'ios' || platform === 'both') {
    logger.line()
    logger.notice('iOS Bundling')
    await command(
      `rm -rf ${dir} && mkdir ${dir} && npx react-native bundle --platform ios --dev false --entry-file index.js --bundle-output ${dir}/index.ios.bundle --assets-dest ${dir} && cd ${dir} && zip -r ../ios-bundle.zip * && cd ../ &&  rm -r dg-bundle`
    )
    logger.line()
    logger.success(`iOS Bundling Done!`)
    logger.success(`Path: ${outputPath}/ios-bundle.zip`.replace('//', '/'))
  }

  logger.line()

  return ''
}

cli.description('Digi code push cli')
cli.name('digi-push')
cli.usage('<command>')
cli.addHelpCommand(true)
cli.helpOption(true)

cli
  .command('bundle')
  //   .argument("[postId]", "ID of post you'd like to retrieve.")
  .option('-o, --outputPath <char>', 'Output path for bundle file.', './')
  .option(
    '-p, --platform <char>',
    'Platform of bundle, Options are android, ios, both.',
    'both'
  )
  .description(
    'Bundle react native Android or iOS or Both and export it to output path.'
  )
  .action(bundle)

cli.parse(process.argv)
